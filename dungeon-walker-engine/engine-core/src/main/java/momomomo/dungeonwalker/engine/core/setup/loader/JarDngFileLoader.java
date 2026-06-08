package momomomo.dungeonwalker.engine.core.setup.loader;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.setup.NoDungeonFilesException;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Slf4j
@RequiredArgsConstructor
public class JarDngFileLoader implements DngFileLoader {

    private static final String LABEL = "---> [DUNGEON LOADER - Jar]";

    private final URL resourceUrl;
    private final String resourcePath;

    @Override
    public @NonNull Stream<String> listFiles() {
        log.debug("{} Getting resources in jar: \"{}\"", LABEL, resourceUrl.getPath());

        final var dungeonFiles = new ArrayList<String>();
        final var jarPath = resourceUrl.getPath().substring(5, resourceUrl.getPath().indexOf("!"));

        log.debug("{} JAR path: \"{}\"", LABEL, jarPath);

        try (final JarFile jar = new JarFile(URLDecoder.decode(jarPath, UTF_8))) {
            final var entries = jar.entries();

            while (entries.hasMoreElements()) {
                final var entry = entries.nextElement();
                final var name = entry.getName();

                if (name.startsWith(resourcePath) && !entry.isDirectory()) {
                    dungeonFiles.add(name.replace(resourcePath, EMPTY));
                }
            }

        } catch (final IOException e) {
            throw new NoDungeonFilesException(e);
        }

        log.debug("{} Found {} dungeon files in dungeons jar folder \"{}\"", LABEL, dungeonFiles.size(), jarPath);

        return dungeonFiles.stream();
    }

}
