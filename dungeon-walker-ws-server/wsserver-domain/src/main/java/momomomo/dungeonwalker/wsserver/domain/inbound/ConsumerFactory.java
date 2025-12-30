package momomomo.dungeonwalker.wsserver.domain.inbound;

public interface ConsumerFactory<M> {

    Consumer<M> create(String groupId);

}
