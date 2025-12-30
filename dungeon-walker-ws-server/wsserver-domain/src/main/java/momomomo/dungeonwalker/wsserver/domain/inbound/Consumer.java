package momomomo.dungeonwalker.wsserver.domain.inbound;

import java.util.List;

public interface Consumer<M> {

    void start();

    void stop();

    List<M> poll();

}
