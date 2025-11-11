package momomomo.dungeonwalker.engine.domain.config;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import static java.util.Objects.requireNonNull;

@Slf4j
public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Nonnull
    @Override
    public PropertySource<?> createPropertySource(@Nullable String name, @NonNull EncodedResource encodedResource) {
        log.debug("[PROPERTY SOURCE FACTORY] creating property source for \"{}\":\"{}\"",
                name, encodedResource.getResource().getFilename());

        final var factory = new YamlPropertiesFactoryBean();

        factory.setResources(encodedResource.getResource());

        return new PropertiesPropertySource(
                requireNonNull(encodedResource.getResource().getFilename()),
                requireNonNull(factory.getObject()));
    }

}
