package com.herculanoleo.spring.me.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.tools.StandardLocation;

import static com.google.testing.compile.CompilationSubject.assertThat;

class GeneratedMapperEnumProcessorTest {

    private static final String ENUM_SOURCE = """
            package com.example.task;

            import com.herculanoleo.spring.me.models.annotation.MapperEnumType;
            import com.herculanoleo.spring.me.models.enums.MapperEnum;

            @MapperEnumType
            public enum TaskStatus implements MapperEnum {
                TODO("T"),
                DONE("D");

                private final String value;

                TaskStatus(String value) {
                    this.value = value;
                }

                @Override
                public String getValue() {
                    return value;
                }
            }
            """;

    @DisplayName("Should generate JSON types, contributor, and ServiceLoader registration")
    @Test
    void generatesTypesForMapperEnumType() {
        Compilation compilation = Compiler.javac()
                .withProcessors(new GeneratedMapperEnumProcessor())
                .compile(JavaFileObjects.forSourceString("com.example.task.TaskStatus", ENUM_SOURCE));

        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("com.example.task.json.TaskStatusJsonSerializer")
                .isNotNull();
        assertThat(compilation)
                .generatedSourceFile("com.example.task.json.TaskStatusJsonDeserializer")
                .isNotNull();
        assertThat(compilation)
                .generatedSourceFile("com.example.task.generated.TaskStatusMapperEnumContributor")
                .isNotNull();
        assertThat(compilation)
                .generatedFile(
                        StandardLocation.CLASS_OUTPUT,
                        "",
                        "META-INF/services/com.herculanoleo.spring.me.spi.MapperEnumTypeContributor")
                .contentsAsUtf8String()
                .isEqualTo("com.example.task.generated.TaskStatusMapperEnumContributor\n");
    }
}
