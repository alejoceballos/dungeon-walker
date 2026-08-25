package momomomo.dungeonwalker.history.transport.gateway;

public interface SaveGateway<M> {

    M save(M entity);

}
