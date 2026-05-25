package com.herculanoleo.spring.me.configuration;

import com.herculanoleo.spring.me.converter.web.MapperEnumFormatterFactory;
import com.herculanoleo.spring.me.models.annotation.EnableMapperEnum;
import com.herculanoleo.spring.me.models.enums.MapperEnum;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.format.Formatter;

import java.util.*;

public class MapperResourceLoader {

    private static final Logger log = LoggerFactory.getLogger(MapperResourceLoader.class);

    private final MapperEnumFormatterFactory formatterFactory = new MapperEnumFormatterFactory();

    private final ApplicationContext applicationContext;

    private final ClassPathScanningCandidateComponentProvider scanner;

    protected Collection<Class<? extends MapperEnum>> classes = List.of();

    private Map<Class<? extends MapperEnum>, Formatter<? extends MapperEnum>> formattersCache = Map.of();

    public MapperResourceLoader(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.scanner = new ClassPathScanningCandidateComponentProvider(false);
        this.scanner.addIncludeFilter(new AssignableTypeFilter(MapperEnum.class));
    }

    public Collection<Class<? extends MapperEnum>> getClasses() {
        return classes;
    }

    @PostConstruct
    public void setup() throws ClassNotFoundException {
        var basePackages = getBasePackages();
        if (basePackages.isEmpty()) {
            log.warn("No @EnableMapperEnum bean found or no base packages configured; MapperEnum scanning was skipped");
            this.classes = List.of();
            this.formattersCache = Map.of();
            return;
        }

        this.classes = findCandidateComponent(basePackages);
        this.formattersCache = buildFormatters(this.classes);
    }

    public Map<Class<? extends MapperEnum>, Formatter<? extends MapperEnum>> serializableEnumFormatter() {
        return formattersCache;
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

    protected Collection<Class<? extends MapperEnum>> findCandidateComponent(Collection<String> basePackages)
            throws ClassNotFoundException {
        var components = new HashSet<Class<? extends MapperEnum>>();

        for (var basePackage : basePackages) {
            var beanDefinitions = scanner.findCandidateComponents(basePackage);
            for (var beanDefinition : beanDefinitions) {
                var classLoader = applicationContext.getClassLoader();
                var clazz = Class.forName(beanDefinition.getBeanClassName(), false, classLoader);
                if (clazz.isEnum() && MapperEnum.class.isAssignableFrom(clazz)) {
                    components.add(clazz.asSubclass(MapperEnum.class));
                }
            }
        }

        return List.copyOf(components);
    }

    protected Collection<String> getBasePackages() {
        var basePackages = new HashSet<String>();

        var entryBean = applicationContext.getBeansWithAnnotation(EnableMapperEnum.class)
                .entrySet()
                .stream()
                .findFirst()
                .orElse(null);

        if (Objects.nonNull(entryBean)) {
            EnableMapperEnum serializableEnum = Objects.requireNonNull(
                    applicationContext.findAnnotationOnBean(entryBean.getKey(), EnableMapperEnum.class)
            );
            basePackages.add(entryBean.getValue().getClass().getPackageName());
            basePackages.addAll(Arrays.asList(serializableEnum.value()));
            basePackages.addAll(Arrays.asList(serializableEnum.basePackages()));
        }

        return basePackages;
    }

}
