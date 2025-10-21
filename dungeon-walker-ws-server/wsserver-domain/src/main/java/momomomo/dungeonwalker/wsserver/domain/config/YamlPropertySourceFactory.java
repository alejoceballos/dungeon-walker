package momomomo.dungeonwalker.wsserver.domain.config;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import static java.util.Objects.requireNonNull;

public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Nonnull
    @Override
    public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource encodedResource) {
        final var factory = new YamlPropertiesFactoryBean();

        factory.setResources(encodedResource.getResource());

        return new PropertiesPropertySource(
                requireNonNull(encodedResource.getResource().getFilename()),
                requireNonNull(factory.getObject()));
    }

}
