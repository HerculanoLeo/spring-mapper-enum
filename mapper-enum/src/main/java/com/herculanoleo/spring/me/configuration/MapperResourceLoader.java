package com.herculanoleo.spring.me.configuration;

import com.herculanoleo.spring.me.converter.web.MapperEnumFormatterFactory;
import com.herculanoleo.spring.me.models.enums.MapperEnum;
import com.herculanoleo.spring.me.spi.MapperEnumTypeContributor;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.format.Formatter;

import java.util.*;

/**
 * Loads compile-time registered {@link com.herculanoleo.spring.me.models.enums.MapperEnum} types
 * and builds shared formatter instances.
 *
 * <p>Contributors are discovered with {@link java.util.ServiceLoader} from
 * {@code META-INF/services/com.herculanoleo.spring.me.spi.MapperEnumTypeContributor}, populated
 * by the annotation processor for each {@link com.herculanoleo.spring.me.models.annotation.MappedEnum}
 * enum. This avoids runtime classpath scanning and supports GraalVM native images when the
 * application is built with the processor enabled.
 *
 * <p>Imported as a Spring bean by {@link com.herculanoleo.spring.me.models.annotation.EnableMapperEnum}
 * and shared with {@link FeignStartConfiguration}.
 */
public class MapperResourceLoader implements BeanClassLoaderAware {

    private static final Logger log = LoggerFactory.getLogger(MapperResourceLoader.class);

    private final MapperEnumFormatterFactory formatterFactory = new MapperEnumFormatterFactory();

    private ClassLoader classLoader = MapperResourceLoader.class.getClassLoader();

    protected Collection<Class<? extends MapperEnum>> classes = List.of();

    private Map<Class<? extends MapperEnum>, Formatter<? extends MapperEnum>> formattersCache = Map.of();

    private List<MapperEnumTypeContributor> contributors = List.of();

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @PostConstruct
    public void setup() {
        this.contributors = loadContributors();
        this.classes = contributors.stream()
                .<Class<? extends MapperEnum>>map(MapperEnumTypeContributor::enumType)
                .toList();
        this.formattersCache = buildFormatters(this.classes);

        if (classes.isEmpty()) {
            log.warn(
                    "No MapperEnum types were registered. Annotate enums with @MappedEnum "
                            + "and ensure the annotation processor runs at compile time."
            );
        }
    }

    /**
     * Returns all registered enum types, sorted by class name.
     *
     * @return immutable view of registered {@link MapperEnum} classes
     */
    public Collection<Class<? extends MapperEnum>> getClasses() {
        return classes;
    }

    /**
     * Returns all {@link MapperEnumTypeContributor} instances loaded from {@code META-INF/services}.
     *
     * @return immutable list of contributors
     */
    public List<MapperEnumTypeContributor> getContributors() {
        return contributors;
    }

    /**
     * Returns a formatter per registered enum type for Spring MVC and Feign.
     *
     * @return immutable map of enum class to formatter
     */
    public Map<Class<? extends MapperEnum>, Formatter<? extends MapperEnum>> serializableEnumFormatter() {
        return formattersCache;
    }

    private List<MapperEnumTypeContributor> loadContributors() {
        var loaded = ServiceLoader.load(MapperEnumTypeContributor.class, classLoader);
        var accepted = new ArrayList<MapperEnumTypeContributor>();
        for (var contributor : loaded) {
            var enumType = contributor.enumType();
            if (enumType == null || !enumType.isEnum() || !MapperEnum.class.isAssignableFrom(enumType)) {
                log.warn("Ignoring MapperEnumTypeContributor {} because {} is not a MapperEnum enum", contributor, enumType);
                continue;
            }
            if (contributor.serializer() == null || contributor.deserializer() == null) {
                log.warn("Ignoring MapperEnumTypeContributor {} because a Jackson adapter is missing", contributor);
                continue;
            }
            accepted.add(contributor);
        }
        accepted.sort(Comparator.comparing(c -> c.enumType().getName()));
        return List.copyOf(accepted);
    }

    private Map<Class<? extends MapperEnum>, Formatter<? extends MapperEnum>> buildFormatters(
            Collection<Class<? extends MapperEnum>> enumClasses
    ) {
        var formatters = new HashMap<Class<? extends MapperEnum>, Formatter<? extends MapperEnum>>();
        for (var clazz : enumClasses) {
            formatters.put(clazz, formatterFactory.getFormatter(clazz));
        }
        return Map.copyOf(formatters);
    }
}
