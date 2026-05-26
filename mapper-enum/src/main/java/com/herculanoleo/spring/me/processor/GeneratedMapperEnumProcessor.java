package com.herculanoleo.spring.me.processor;

import com.google.auto.service.AutoService;
import com.herculanoleo.spring.me.models.annotation.MapperEnumDBConverter;
import com.herculanoleo.spring.me.models.annotation.MapperEnumType;
import com.herculanoleo.spring.me.models.enums.MapperEnum;
import com.herculanoleo.spring.me.spi.MapperEnumTypeContributor;
import com.palantir.javapoet.*;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AutoService(Processor.class)
@SupportedAnnotationTypes({
        GeneratedMapperEnumProcessor.MAPPER_ENUM_TYPE,
        GeneratedMapperEnumProcessor.MAPPER_ENUM_DB_CONVERTER
})
public class GeneratedMapperEnumProcessor extends AbstractProcessor {

    static final String MAPPER_ENUM_TYPE = "com.herculanoleo.spring.me.models.annotation.MapperEnumType";
    static final String MAPPER_ENUM_DB_CONVERTER = "com.herculanoleo.spring.me.models.annotation.MapperEnumDBConverter";
    private static final String CONTRIBUTOR_SERVICE_RESOURCE =
            "META-INF/services/com.herculanoleo.spring.me.spi.MapperEnumTypeContributor";

    private static final ClassName MAPPER_ENUM = ClassName.get(MapperEnum.class);
    private static final ClassName JSON_PARSER = ClassName.get("tools.jackson.core", "JsonParser");
    private static final ClassName JSON_TOKEN = ClassName.get("tools.jackson.core", "JsonToken");
    private static final ClassName JSON_GENERATOR = ClassName.get("tools.jackson.core", "JsonGenerator");
    private static final ClassName JACKSON_EXCEPTION = ClassName.get("tools.jackson.core", "JacksonException");
    private static final ClassName DESERIALIZATION_CONTEXT = ClassName.get("tools.jackson.databind", "DeserializationContext");
    private static final ClassName SERIALIZATION_CONTEXT = ClassName.get("tools.jackson.databind", "SerializationContext");
    private static final ClassName VALUE_DESERIALIZER = ClassName.get("tools.jackson.databind", "ValueDeserializer");
    private static final ClassName VALUE_SERIALIZER = ClassName.get("tools.jackson.databind", "ValueSerializer");
    private static final ClassName CONTRIBUTOR = ClassName.get(MapperEnumTypeContributor.class);

    private boolean jpaAvailable = false;

    private final Set<String> contributorClassNames = new LinkedHashSet<>();

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        try {
            Class.forName("jakarta.persistence.AttributeConverter");
            jpaAvailable = true;
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "MapperEnum JPA converter generation enabled");
        } catch (Exception ignored) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "MapperEnum JPA converter generation disabled (no jakarta.persistence)");
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        var elements = Stream.concat(
                roundEnv.getElementsAnnotatedWith(MapperEnumType.class).stream(),
                roundEnv.getElementsAnnotatedWith(MapperEnumDBConverter.class).stream()
        ).collect(Collectors.toCollection(LinkedHashSet::new));

        for (var element : elements) {
            if (element.getKind() == ElementKind.ENUM && element instanceof TypeElement enumElement) {
                processEnum(enumElement);
            } else if (element.getKind() == ElementKind.ENUM) {
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

        var basePackage = processingEnv.getElementUtils().getPackageOf(enumElement).toString();
        var enumName = enumElement.getSimpleName().toString();
        var enumClass = ClassName.get(basePackage, enumName);

        try {
            generateJsonSerializer(basePackage, enumName, enumClass);
            generateJsonDeserializer(basePackage, enumName, enumClass);
            var contributorClass = generateContributor(basePackage, enumName, enumClass);
            contributorClassNames.add(contributorClass.canonicalName());

            if (jpaAvailable && hasAnnotation(enumElement, MapperEnumDBConverter.class)) {
                generateJpaConverter(basePackage, enumName, enumClass);
            }
        } catch (IOException e) {
            error(enumElement, e.getMessage());
        }
    }

    private void generateJsonSerializer(String basePackage, String enumName, ClassName enumClass) throws IOException {
        var serializerClass = ClassName.get(basePackage + ".json", enumName + "JsonSerializer");
        var typeSpec = TypeSpec.classBuilder(enumName + "JsonSerializer")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(ParameterizedTypeName.get(VALUE_SERIALIZER, enumClass))
                .addField(FieldSpec.builder(serializerClass, "INSTANCE", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .build())
                .addMethod(MethodSpec.methodBuilder("serialize")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(enumClass, "value")
                        .addParameter(JSON_GENERATOR, "gen")
                        .addParameter(SERIALIZATION_CONTEXT, "ctxt")
                        .addException(JACKSON_EXCEPTION)
                        .addStatement("gen.writeString(value.getValue())")
                        .build())
                .addStaticBlock(CodeBlock.builder()
                        .addStatement("INSTANCE = new $T()", serializerClass)
                        .build())
                .build();

        JavaFile.builder(basePackage + ".json", typeSpec).build().writeTo(processingEnv.getFiler());
    }

    private void generateJsonDeserializer(String basePackage, String enumName, ClassName enumClass) throws IOException {
        var deserializerClass = ClassName.get(basePackage + ".json", enumName + "JsonDeserializer");
        var typeSpec = TypeSpec.classBuilder(enumName + "JsonDeserializer")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(ParameterizedTypeName.get(VALUE_DESERIALIZER, enumClass))
                .addField(FieldSpec.builder(deserializerClass, "INSTANCE", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .build())
                .addMethod(MethodSpec.methodBuilder("deserialize")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(enumClass)
                        .addParameter(JSON_PARSER, "jsonParser")
                        .addParameter(DESERIALIZATION_CONTEXT, "context")
                        .beginControlFlow("if (jsonParser.currentToken() == $T.VALUE_NULL)", JSON_TOKEN)
                        .addStatement("return null")
                        .endControlFlow()
                        .addStatement(
                                "return $T.fromValue(jsonParser.getString(), $T.class)",
                                MAPPER_ENUM,
                                enumClass
                        )
                        .build())
                .addStaticBlock(CodeBlock.builder()
                        .addStatement("INSTANCE = new $T()", deserializerClass)
                        .build())
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

    private void generateJpaConverter(String basePackage, String enumName, ClassName enumClass) throws IOException {
        var converterClassName = ClassName.get(AttributeConverter.class.getPackageName(), AttributeConverter.class.getSimpleName());
        var elementStringClassName = ClassName.get(String.class);
        var parameterizedTypeName = ParameterizedTypeName.get(converterClassName, enumClass, elementStringClassName);

        var generatedClass = TypeSpec.classBuilder(enumName + "Converter")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(AnnotationSpec.builder(Converter.class)
                        .addMember("autoApply", "true")
                        .build())
                .addSuperinterface(parameterizedTypeName)
                .addMethod(MethodSpec.methodBuilder("convertToDatabaseColumn")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(String.class)
                        .addParameter(enumClass, "value")
                        .beginControlFlow("if (null != value)")
                        .addStatement("return value.getValue()")
                        .endControlFlow()
                        .addStatement("return null")
                        .build())
                .addMethod(MethodSpec.methodBuilder("convertToEntityAttribute")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(enumClass)
                        .addParameter(String.class, "value")
                        .beginControlFlow("if ($T.isNotBlank(value))", StringUtils.class)
                        .addStatement("return $T.fromValue(value, $T.class)", MAPPER_ENUM, enumClass)
                        .endControlFlow()
                        .addStatement("return null")
                        .build())
                .build();

        JavaFile.builder(basePackage + ".converters", generatedClass).build().writeTo(processingEnv.getFiler());
    }

    private void writeContributorServiceFile(Set<String> contributorNames) throws IOException {
        var resource = processingEnv.getFiler().createResource(
                StandardLocation.CLASS_OUTPUT, "", CONTRIBUTOR_SERVICE_RESOURCE);
        try (Writer writer = resource.openWriter()) {
            for (var name : contributorNames) {
                writer.write(name);
                writer.write('\n');
            }
        }
    }

    private boolean implementsMapperEnum(TypeElement typeElement) {
        TypeMirror mapperEnumType = processingEnv.getElementUtils()
                .getTypeElement(MapperEnum.class.getCanonicalName())
                .asType();
        return processingEnv.getTypeUtils().isAssignable(typeElement.asType(), mapperEnumType);
    }

    private boolean hasAnnotation(TypeElement typeElement, Class<?> annotationType) {
        return typeElement.getAnnotationMirrors().stream()
                .map(AnnotationMirror::getAnnotationType)
                .map(javax.lang.model.type.TypeMirror::toString)
                .anyMatch(annotationType.getCanonicalName()::equals);
    }

    private void error(Element element, String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
    }
}
