package com.herculanoleo.spring.me.configuration;

import com.herculanoleo.spring.me.converter.web.MapperEnumFormatterFactory;
import com.herculanoleo.spring.me.models.enums.MapperEnum;
import com.herculanoleo.spring.me.spi.MapperEnumTypeContributor;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.Formatter;

import java.util.*;

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
                .map(MapperEnumTypeContributor::enumType)
                .toList();
        this.formattersCache = buildFormatters(this.classes);

        if (classes.isEmpty()) {
            log.warn(
                    "No MapperEnum types were registered. Annotate enums with @MapperEnumType or @MapperEnumDBConverter "
                            + "and ensure the annotation processor runs at compile time."
            );
        }
    }

    public Collection<Class<? extends MapperEnum>> getClasses() {
        return classes;
    }

    public List<MapperEnumTypeContributor> getContributors() {
        return contributors;
    }

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
