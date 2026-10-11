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

            import com.herculanoleo.spring.me.models.annotation.MappedEnum;
            import com.herculanoleo.spring.me.models.enums.MapperEnum;

            @MappedEnum
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
        Compilation compilation = compile(ENUM_SOURCE);

        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("com.example.task.json.TaskStatusJsonDeserializer")
                .contentsAsUtf8String()
                .contains("MapperEnumJson.read");
        assertThat(compilation)
                .generatedFile(
                        StandardLocation.CLASS_OUTPUT,
                        "",
                        "META-INF/services/com.herculanoleo.spring.me.spi.MapperEnumTypeContributor")
                .contentsAsUtf8String()
                .isEqualTo("com.example.task.generated.TaskStatusMapperEnumContributor\n");
    }

    @DisplayName("Should fail when two constants share a mapped value")
    @Test
    void rejectsDuplicateValues() {
        Compilation compilation = compile("""
                package com.example.task;

                import com.herculanoleo.spring.me.models.annotation.MappedEnum;
                import com.herculanoleo.spring.me.models.enums.MapperEnum;

                @MappedEnum
                public enum TaskStatus implements MapperEnum {
                    TODO("T"),
                    DONE("T");

                    private final String value;

                    TaskStatus(String value) {
                        this.value = value;
                    }

                    @Override
                    public String getValue() {
                        return value;
                    }
                }
                """);

        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("Duplicate MapperEnum value");
    }

    @DisplayName("Should fail when a mapped value equals another constant name")
    @Test
    void rejectsValueEqualToAnotherName() {
        Compilation compilation = compile("""
                package com.example.task;

                import com.herculanoleo.spring.me.models.annotation.MappedEnum;
                import com.herculanoleo.spring.me.models.enums.MapperEnum;

                @MappedEnum
                public enum TaskStatus implements MapperEnum {
                    TODO("DONE"),
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
                """);

        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("collides with the name");
    }

    @DisplayName("Should generate a JPA converter with the requested autoApply flag")
    @Test
    void generatesJpaConverter() {
        Compilation compilation = compile("""
                package com.example.task;

                import com.herculanoleo.spring.me.models.annotation.MappedEnum;
                import com.herculanoleo.spring.me.models.enums.MapperEnum;

                @MappedEnum(autoApply = false)
                public enum TaskStatus implements MapperEnum {
                    TODO("T");

                    private final String value;

                    TaskStatus(String value) {
                        this.value = value;
                    }

                    @Override
                    public String getValue() {
                        return value;
                    }
                }
                """);

        assertThat(compilation).succeeded();
        var converter = assertThat(compilation)
                .generatedSourceFile("com.example.task.converters.TaskStatusConverter")
                .contentsAsUtf8String();
        converter.contains("autoApply = false");
        converter.contains("value.isBlank()");
    }

    @DisplayName("Should skip the JPA converter when persist is false")
    @Test
    void skipsJpaConverterWhenPersistIsDisabled() {
        Compilation compilation = compile("""
                package com.example.task;

                import com.herculanoleo.spring.me.models.annotation.MappedEnum;
                import com.herculanoleo.spring.me.models.enums.MapperEnum;

                @MappedEnum(persist = false)
                public enum TaskStatus implements MapperEnum {
                    TODO("T");

                    private final String value;

                    TaskStatus(String value) {
                        this.value = value;
                    }

                    @Override
                    public String getValue() {
                        return value;
                    }
                }
                """);

        assertThat(compilation).succeeded();
        org.junit.jupiter.api.Assertions.assertFalse(
                compilation.generatedSourceFiles().stream().anyMatch(file -> file.getName().contains("TaskStatusConverter"))
        );
    }

    private static Compilation compile(String source) {
        return Compiler.javac()
                .withProcessors(new GeneratedMapperEnumProcessor())
                .compile(JavaFileObjects.forSourceString("com.example.task.TaskStatus", source));
    }
}
