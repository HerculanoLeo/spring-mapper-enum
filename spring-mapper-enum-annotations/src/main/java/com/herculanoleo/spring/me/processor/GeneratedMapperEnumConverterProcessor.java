package com.herculanoleo.spring.me.processor;

import com.herculanoleo.spring.me.models.annotation.MapperEnumDBConverter;
import com.herculanoleo.spring.me.models.enums.MapperEnum;
import com.squareup.javapoet.*;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class GeneratedMapperEnumConverterProcessor extends AbstractProcessor {

    private boolean isRunnable = false;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        try {
            Class.forName("jakarta.persistence.AttributeConverter");
            isRunnable = true;
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Jakarta Persistence detected. Mapper Enums Converter will be processing");
        } catch (Exception ignored) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Jakarta Persistence not detected. Skipping processor");
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!isRunnable) {
            return false;
        }

        var elements = roundEnv.getElementsAnnotatedWith(MapperEnumDBConverter.class);

        for (var element : elements) {
            if (element.getKind() == ElementKind.ENUM && element instanceof TypeElement e) {
                generateConverterForEnum(e);
            }
        }

        return true;
    }

    protected void generateConverterForEnum(TypeElement typeElement) {
        try {
            var basePackageName = processingEnv.getElementUtils().getPackageOf(typeElement).toString();
            var className = typeElement.getSimpleName().toString() + "EnumConverter";

            var converterClassName = ClassName.get(AttributeConverter.class.getPackageName(), AttributeConverter.class.getSimpleName());
            var elementClassName = ClassName.get(basePackageName, typeElement.getSimpleName().toString());
            var elementStringClassName = ClassName.get(String.class);

            var parameterizedTypeName = ParameterizedTypeName.get(converterClassName, elementClassName, elementStringClassName);

            var generatedClass = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(AnnotationSpec.builder(Converter.class)
                            .addMember("autoApply", "true")
                            .build()
                    )
                    .addSuperinterface(parameterizedTypeName)
                    .addMethod(MethodSpec
                            .methodBuilder("convertToDatabaseColumn")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(String.class)
                            .addParameter(elementClassName, "value")
                            .beginControlFlow("if (null != value)")
                            .addStatement("return $L.getValue()", "value")
                            .endControlFlow()
                            .addStatement("return $L", "null")
                            .build())
                    .addMethod(MethodSpec
                            .methodBuilder("convertToEntityAttribute")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(elementClassName)
                            .addParameter(String.class, "value")
                            .beginControlFlow("if ($T.isNotBlank(value))", StringUtils.class)
                            .addStatement("return $T.fromValue(value, $T.class)", MapperEnum.class, elementClassName)
                            .endControlFlow()
                            .addStatement("return $L", "null")
                            .build())
                    .build();

            JavaFile javaFile = JavaFile.builder(basePackageName + ".converters", generatedClass)
                    .build();

            javaFile.writeTo(processingEnv.getFiler());
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getLocalizedMessage());
        }
    }

}
