package momomomo.dungeonwalker.engine.domain.inbound;

public interface MessageReceiver<M> {

    void onReceive(M message);

}
