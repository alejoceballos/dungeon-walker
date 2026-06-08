package momomomo.dungeonwalker.engine.core.setup.loader;

import lombok.NonNull;

import java.util.stream.Stream;

public interface DngFileLoader {

    @NonNull
    Stream<String> listFiles();

}
