package momomomo.dungeonwalker.history.domain.inbound.data;

public interface WalkerLogInput extends HistoryLogInput {

    String getWalkerId();

    CoordinatesLogInput getFrom();

    CoordinatesLogInput getTo();

    String getReason();

}
