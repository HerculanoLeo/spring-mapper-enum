package com.herculanoleo.spring.me.configuration;

import com.herculanoleo.spring.me.converter.web.MapperEnumFormatterFactory;
import com.herculanoleo.spring.me.models.annotation.EnableMapperEnum;
import com.herculanoleo.spring.me.models.enums.MapperEnum;
import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.format.Formatter;

import java.util.*;

public class ResourceLoader {

    private final MapperEnumFormatterFactory formatterFactory = new MapperEnumFormatterFactory();

    private final ApplicationContext applicationContext;

    private final ClassPathScanningCandidateComponentProvider scanner;

    protected Collection<Class<? extends MapperEnum>> classes;

    public ResourceLoader(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.scanner = new ClassPathScanningCandidateComponentProvider(false);
        this.scanner.addIncludeFilter(new AssignableTypeFilter(MapperEnum.class));
    }

    public Collection<Class<? extends MapperEnum>> getClasses() {
        return classes;
    }

    @PostConstruct
    public void setup() throws ClassNotFoundException {
        this.classes = findCandidateComponent();
    }

    public Map<Class<? extends MapperEnum>, Formatter<? extends MapperEnum>> serializableEnumFormatter() {
        var formatters = new HashMap<Class<? extends MapperEnum>, Formatter<? extends MapperEnum>>();

        for (var clazz : classes) {
            if (clazz.isEnum()) {
                formatters.put(clazz, formatterFactory.getFormatter(clazz));
            }
        }

        return formatters;
    }

    protected Collection<Class<? extends MapperEnum>> findCandidateComponent() throws ClassNotFoundException {
        var components = new HashSet<Class<? extends MapperEnum>>();
        var basePackages = getBasePackages();

        for (var basePackage : basePackages) {
            var beanDefinitions = scanner.findCandidateComponents(basePackage);
            for (var beanDefinition : beanDefinitions) {
                var clazz = Class.forName(beanDefinition.getBeanClassName());
                if (MapperEnum.class.isAssignableFrom(clazz)) {
                    components.add(clazz.asSubclass(MapperEnum.class));
                }
            }
        }

        return components;
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
