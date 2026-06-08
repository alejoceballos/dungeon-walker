package momomomo.dungeonwalker.engine.core.setup.loader;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.setup.NoDungeonFilesException;

import java.io.File;
import java.net.URL;
import java.util.stream.Stream;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Slf4j
@RequiredArgsConstructor
public class SystemDngFileLoader implements DngFileLoader {

    private static final String LABEL = "---> [DUNGEON LOADER - File System]";

    private final URL resourceUrl;

    @Override
    public @NonNull Stream<String> listFiles() {
        log.debug("{} Getting resources directory: \"{}\"", LABEL, resourceUrl.getPath());

        final var dungeonsDir = new File(resourceUrl.getPath());

        if (!dungeonsDir.exists() || !dungeonsDir.isDirectory()) {
            throw new NoDungeonFilesException("Dungeons folder not found or is not a directory");
        }

        final var dungeonFiles = dungeonsDir.list();

        if (isEmpty(dungeonFiles)) {
            throw new NoDungeonFilesException("No files found in dungeons folder");
        }

        log.debug("{} Found {} dungeon files in dungeons folder \"{}\"", LABEL, dungeonFiles.length, resourceUrl.getPath());

        return Stream.of(dungeonFiles);
    }

}
