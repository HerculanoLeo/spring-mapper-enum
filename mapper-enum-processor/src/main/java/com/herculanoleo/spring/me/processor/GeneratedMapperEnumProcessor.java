package com.herculanoleo.spring.me.processor;

import com.google.auto.service.AutoService;
import com.herculanoleo.spring.me.models.annotation.MappedEnum;
import com.herculanoleo.spring.me.models.enums.MapperEnum;
import com.herculanoleo.spring.me.spi.MapperEnumTypeContributor;
import com.palantir.javapoet.AnnotationSpec;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeSpec;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.Trees;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Annotation processor that runs at compile time for enums annotated with {@link MappedEnum}.
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes(GeneratedMapperEnumProcessor.MAPPED_ENUM)
public class GeneratedMapperEnumProcessor extends AbstractProcessor {

    static final String MAPPED_ENUM = "com.herculanoleo.spring.me.models.annotation.MappedEnum";

    private static final String CONTRIBUTOR_SERVICE_RESOURCE =
            "META-INF/services/com.herculanoleo.spring.me.spi.MapperEnumTypeContributor";

    private static final ClassName MAPPER_ENUM_JSON = ClassName.get(
            "com.herculanoleo.spring.me.converter.json",
            "MapperEnumJson"
    );
    private static final ClassName JSON_PARSER = ClassName.get("tools.jackson.core", "JsonParser");
    private static final ClassName JSON_GENERATOR = ClassName.get("tools.jackson.core", "JsonGenerator");
    private static final ClassName JACKSON_EXCEPTION = ClassName.get("tools.jackson.core", "JacksonException");
    private static final ClassName DESERIALIZATION_CONTEXT = ClassName.get("tools.jackson.databind", "DeserializationContext");
    private static final ClassName SERIALIZATION_CONTEXT = ClassName.get("tools.jackson.databind", "SerializationContext");
    private static final ClassName VALUE_DESERIALIZER = ClassName.get("tools.jackson.databind", "ValueDeserializer");
    private static final ClassName VALUE_SERIALIZER = ClassName.get("tools.jackson.databind", "ValueSerializer");
    private static final ClassName CONTRIBUTOR = ClassName.get(MapperEnumTypeContributor.class);
    private static final ClassName PERSISTENCE_CONVERTER = ClassName.get("jakarta.persistence", "Converter");
    private static final ClassName ATTRIBUTE_CONVERTER = ClassName.get("jakarta.persistence", "AttributeConverter");

    private final Set<String> contributorClassNames = new LinkedHashSet<>();

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        var elements = new LinkedHashSet<Element>();
        elements.addAll(roundEnv.getElementsAnnotatedWith(MappedEnum.class));

        for (var element : elements) {
            if (element.getKind() == ElementKind.ENUM && element instanceof TypeElement enumElement) {
                processEnum(enumElement);
            } else if (element.getKind() != ElementKind.ENUM) {
                error(element, "MapperEnum annotations must be placed on enum types");
            }
        }

        if (roundEnv.processingOver() && !contributorClassNames.isEmpty()) {
            try {
                writeContributorServiceFile(contributorClassNames);
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            }
        }

        return false;
    }

    private void processEnum(TypeElement enumElement) {
        if (!implementsMapperEnum(enumElement)) {
            error(enumElement, "Enum must implement MapperEnum");
            return;
        }

        var values = readConstantValues(enumElement);
        if (values != null) {
            validateValues(enumElement, values);
        }
        validateFallback(enumElement);
        warnWhenFallbackExists(enumElement);

        var basePackage = processingEnv.getElementUtils().getPackageOf(enumElement).toString();
        var enumName = enumElement.getSimpleName().toString();
        var enumClass = ClassName.get(basePackage, enumName);

        try {
            generateJsonSerializer(basePackage, enumName, enumClass);
            generateJsonDeserializer(basePackage, enumName, enumClass);
            var contributorClass = generateContributor(basePackage, enumName, enumClass);
            contributorClassNames.add(contributorClass.canonicalName());

            if (jpaAvailable() && persist(enumElement)) {
                generateJpaConverter(basePackage, enumName, enumClass, autoApply(enumElement));
            }
        } catch (IOException e) {
            error(enumElement, e.getMessage());
        }
    }

    private void validateValues(TypeElement enumElement, Map<String, String> constantToValue) {
        var seenValues = new LinkedHashMap<String, String>();
        for (var entry : constantToValue.entrySet()) {
            var constant = entry.getKey();
            var value = entry.getValue();
            var previous = seenValues.putIfAbsent(value, constant);
            if (previous != null) {
                error(enumElement, "Duplicate MapperEnum value \"" + value + "\" on " + previous + " and " + constant);
            }
            for (var other : constantToValue.keySet()) {
                if (!other.equals(constant) && other.equals(value)) {
                    error(enumElement, "MapperEnum value \"" + value + "\" on " + constant + " collides with the name of " + other);
                }
            }
        }
    }

    private void validateFallback(TypeElement enumElement) {
        var fallback = fallbackName(enumElement);
        if (fallback == null || fallback.isBlank()) {
            return;
        }
        var names = enumElement.getEnclosedElements().stream()
                .filter(element -> element.getKind() == ElementKind.ENUM_CONSTANT)
                .map(element -> element.getSimpleName().toString())
                .toList();
        if (!names.contains(fallback)) {
            error(enumElement, "MappedEnum.fallback \"" + fallback + "\" is not a constant of " + enumElement.getSimpleName());
        }
    }

    private void warnWhenFallbackExists(TypeElement enumElement) {
        var annotationFallback = fallbackName(enumElement);
        if (annotationFallback != null && !annotationFallback.isBlank()) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.WARNING,
                    "MapperEnum fallback \"" + annotationFallback + "\" maps unknown values to a constant. Do not use this for authorization enums.",
                    enumElement
            );
        }
        if (overridesGetGeneric(enumElement)) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.WARNING,
                    "getGeneric() maps unknown values to a constant. Do not use this for authorization enums.",
                    enumElement
            );
        }
    }

    private boolean overridesGetGeneric(TypeElement enumElement) {
        if (hasGetGeneric(enumElement.getEnclosedElements())) {
            return true;
        }
        return enumElement.getEnclosedElements().stream()
                .filter(element -> element.getKind() == ElementKind.ENUM_CONSTANT)
                .anyMatch(constant -> hasGetGeneric(constant.getEnclosedElements()));
    }

    private boolean hasGetGeneric(List<? extends Element> elements) {
        return elements.stream().anyMatch(element ->
                element.getKind() == ElementKind.METHOD && element.getSimpleName().contentEquals("getGeneric"));
    }

    private Map<String, String> readConstantValues(TypeElement enumElement) {
        Trees trees;
        try {
            trees = Trees.instance(processingEnv);
        } catch (RuntimeException ex) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "MapperEnum could not read constant values for collision checks: " + ex.getMessage(),
                    enumElement
            );
            return null;
        }

        var values = new LinkedHashMap<String, String>();
        for (var element : enumElement.getEnclosedElements()) {
            if (element.getKind() != ElementKind.ENUM_CONSTANT || !(element instanceof VariableElement constant)) {
                continue;
            }
            var path = trees.getPath(constant);
            if (path == null || !(path.getLeaf() instanceof VariableTree variable) || !(variable.getInitializer() instanceof NewClassTree creation)) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.NOTE,
                        "MapperEnum value of " + constant.getSimpleName() + " is not a string literal; collision checks skipped for it",
                        constant
                );
                continue;
            }
            if (creation.getArguments().isEmpty() || !(creation.getArguments().get(0) instanceof LiteralTree literal) || !(literal.getValue() instanceof String text)) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.NOTE,
                        "MapperEnum value of " + constant.getSimpleName() + " is not a string literal; collision checks skipped for it",
                        constant
                );
                continue;
            }
            values.put(constant.getSimpleName().toString(), text);
        }
        return values;
    }

    private void generateJsonSerializer(String basePackage, String enumName, ClassName enumClass) throws IOException {
        var serializerClass = ClassName.get(basePackage + ".json", enumName + "JsonSerializer");
        var typeSpec = TypeSpec.classBuilder(enumName + "JsonSerializer")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(ParameterizedTypeName.get(VALUE_SERIALIZER, enumClass))
                .addField(FieldSpec.builder(serializerClass, "INSTANCE", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL).build())
                .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build())
                .addMethod(MethodSpec.methodBuilder("serialize")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(enumClass, "value")
                        .addParameter(JSON_GENERATOR, "gen")
                        .addParameter(SERIALIZATION_CONTEXT, "ctxt")
                        .addException(JACKSON_EXCEPTION)
                        .addStatement("gen.writeString(value.getValue())")
                        .build())
                .addStaticBlock(CodeBlock.builder().addStatement("INSTANCE = new $T()", serializerClass).build())
                .build();

        JavaFile.builder(basePackage + ".json", typeSpec).build().writeTo(processingEnv.getFiler());
    }

    private void generateJsonDeserializer(String basePackage, String enumName, ClassName enumClass) throws IOException {
        var deserializerClass = ClassName.get(basePackage + ".json", enumName + "JsonDeserializer");
        var typeSpec = TypeSpec.classBuilder(enumName + "JsonDeserializer")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(ParameterizedTypeName.get(VALUE_DESERIALIZER, enumClass))
                .addField(FieldSpec.builder(deserializerClass, "INSTANCE", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL).build())
                .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build())
                .addMethod(MethodSpec.methodBuilder("deserialize")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(enumClass)
                        .addParameter(JSON_PARSER, "jsonParser")
                        .addParameter(DESERIALIZATION_CONTEXT, "context")
                        .addStatement("return $T.read(jsonParser, context, $T.class)", MAPPER_ENUM_JSON, enumClass)
                        .build())
                .addStaticBlock(CodeBlock.builder().addStatement("INSTANCE = new $T()", deserializerClass).build())
                .build();

        JavaFile.builder(basePackage + ".json", typeSpec).build().writeTo(processingEnv.getFiler());
    }

    private ClassName generateContributor(String basePackage, String enumName, ClassName enumClass) throws IOException {
        var contributorPackage = basePackage + ".generated";
        var serializerClass = ClassName.get(basePackage + ".json", enumName + "JsonSerializer");
        var deserializerClass = ClassName.get(basePackage + ".json", enumName + "JsonDeserializer");
        var contributorClass = ClassName.get(contributorPackage, enumName + "MapperEnumContributor");

        var typeSpec = TypeSpec.classBuilder(enumName + "MapperEnumContributor")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(CONTRIBUTOR)
                .addMethod(MethodSpec.methodBuilder("enumType")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ParameterizedTypeName.get(ClassName.get(Class.class), enumClass))
                        .addStatement("return $T.class", enumClass)
                        .build())
                .addMethod(MethodSpec.methodBuilder("serializer")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(VALUE_SERIALIZER)
                        .addStatement("return $T.INSTANCE", serializerClass)
                        .build())
                .addMethod(MethodSpec.methodBuilder("deserializer")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(VALUE_DESERIALIZER)
                        .addStatement("return $T.INSTANCE", deserializerClass)
                        .build())
                .build();

        JavaFile.builder(contributorPackage, typeSpec).build().writeTo(processingEnv.getFiler());
        return contributorClass;
    }

    private void generateJpaConverter(String basePackage, String enumName, ClassName enumClass, boolean autoApply) throws IOException {
        var parameterizedTypeName = ParameterizedTypeName.get(ATTRIBUTE_CONVERTER, enumClass, ClassName.get(String.class));
        var generatedClass = TypeSpec.classBuilder(enumName + "Converter")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(AnnotationSpec.builder(PERSISTENCE_CONVERTER)
                        .addMember("autoApply", "$L", autoApply)
                        .build())
                .addSuperinterface(parameterizedTypeName)
                .addMethod(MethodSpec.methodBuilder("convertToDatabaseColumn")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(String.class)
                        .addParameter(enumClass, "value")
                        .beginControlFlow("if (value != null)")
                        .addStatement("return value.getValue()")
                        .endControlFlow()
                        .addStatement("return null")
                        .build())
                .addMethod(MethodSpec.methodBuilder("convertToEntityAttribute")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(enumClass)
                        .addParameter(String.class, "value")
                        .beginControlFlow("if (value != null && !value.isBlank())")
                        .addStatement("return $T.fromValue(value, $T.class)", ClassName.get(MapperEnum.class), enumClass)
                        .endControlFlow()
                        .addStatement("return null")
                        .build())
                .build();

        JavaFile.builder(basePackage + ".converters", generatedClass).build().writeTo(processingEnv.getFiler());
    }

    private void writeContributorServiceFile(Set<String> contributorNames) throws IOException {
        var merged = new LinkedHashSet<String>();
        try {
            FileObject existing = processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", CONTRIBUTOR_SERVICE_RESOURCE);
            try (var reader = new BufferedReader(new InputStreamReader(existing.openInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.isBlank()) {
                        merged.add(line.trim());
                    }
                }
            }
        } catch (Exception ignored) {
            // First compilation has no previous service file.
        }
        merged.addAll(contributorNames);

        var resource = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", CONTRIBUTOR_SERVICE_RESOURCE);
        try (Writer writer = resource.openWriter()) {
            for (var name : merged) {
                writer.write(name);
                writer.write('\n');
            }
        }
    }

    private boolean jpaAvailable() {
        return processingEnv.getElementUtils().getTypeElement("jakarta.persistence.AttributeConverter") != null;
    }

    private boolean implementsMapperEnum(TypeElement typeElement) {
        TypeMirror mapperEnumType = processingEnv.getElementUtils()
                .getTypeElement(MapperEnum.class.getCanonicalName())
                .asType();
        return processingEnv.getTypeUtils().isAssignable(typeElement.asType(), mapperEnumType);
    }

    private boolean persist(TypeElement typeElement) {
        return booleanMember(typeElement, "persist", true);
    }

    private boolean autoApply(TypeElement typeElement) {
        return booleanMember(typeElement, "autoApply", true);
    }

    private boolean booleanMember(TypeElement typeElement, String name, boolean defaultValue) {
        for (var mirror : typeElement.getAnnotationMirrors()) {
            if (!MAPPED_ENUM.equals(mirror.getAnnotationType().toString())) {
                continue;
            }
            for (var entry : mirror.getElementValues().entrySet()) {
                if (entry.getKey().getSimpleName().contentEquals(name)) {
                    return Boolean.TRUE.equals(entry.getValue().getValue());
                }
            }
        }
        return defaultValue;
    }

    private String fallbackName(TypeElement typeElement) {
        for (var mirror : typeElement.getAnnotationMirrors()) {
            if (!MAPPED_ENUM.equals(mirror.getAnnotationType().toString())) {
                continue;
            }
            for (var entry : mirror.getElementValues().entrySet()) {
                if (entry.getKey().getSimpleName().contentEquals("fallback") && entry.getValue().getValue() instanceof String name) {
                    return name;
                }
            }
        }
        return "";
    }

    private void error(Element element, String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
    }
}
