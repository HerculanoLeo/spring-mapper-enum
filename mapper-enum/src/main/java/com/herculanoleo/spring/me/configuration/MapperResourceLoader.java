package com.herculanoleo.spring.me.configuration;

import com.herculanoleo.spring.me.converter.web.MapperEnumFormatterFactory;
import com.herculanoleo.spring.me.models.enums.MapperEnum;
import com.herculanoleo.spring.me.spi.MapperEnumTypeContributor;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.Formatter;

import java.util.*;

/**
 * Loads compile-time registered {@link com.herculanoleo.spring.me.models.enums.MapperEnum} types
 * and builds shared formatter instances.
 *
 * <p>Contributors are discovered with {@link java.util.ServiceLoader} from
 * {@code META-INF/services/com.herculanoleo.spring.me.spi.MapperEnumTypeContributor}, populated
 * by the annotation processor for each {@link com.herculanoleo.spring.me.models.annotation.MapperEnumType}
 * enum. This avoids runtime classpath scanning and supports GraalVM native images when the
 * application is built with the processor enabled.
 *
 * <p>Imported as a Spring bean by {@link com.herculanoleo.spring.me.models.annotation.EnableMapperEnum}
 * and shared with {@link FeignStartConfiguration}.
 */
public class MapperResourceLoader {

    private static final Logger log = LoggerFactory.getLogger(MapperResourceLoader.class);

    private final MapperEnumFormatterFactory formatterFactory = new MapperEnumFormatterFactory();

    protected Collection<Class<? extends MapperEnum>> classes = List.of();

    private Map<Class<? extends MapperEnum>, Formatter<? extends MapperEnum>> formattersCache = Map.of();

    private List<MapperEnumTypeContributor> contributors = List.of();

    @PostConstruct
    public void setup() {
        this.contributors = loadContributors();
        this.classes = contributors.stream()
                .<Class<? extends MapperEnum>>map(MapperEnumTypeContributor::enumType)
                .toList();
        this.formattersCache = buildFormatters(this.classes);

        if (classes.isEmpty()) {
            log.warn(
                    "No MapperEnum types were registered. Annotate enums with @MapperEnumType or @MapperEnumDBConverter "
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
        var loaded = ServiceLoader.load(MapperEnumTypeContributor.class);
        var contributors = new ArrayList<MapperEnumTypeContributor>();
        for (var contributor : loaded) {
            contributors.add(contributor);
        }
        contributors.sort(Comparator.comparing(c -> c.enumType().getName()));
        return List.copyOf(contributors);
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
