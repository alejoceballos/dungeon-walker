package momomomo.dungeonwalker.history.transport.mapper.walker;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import momomomo.dungeonwalker.history.domain.model.walker.Walker;
import momomomo.dungeonwalker.history.domain.model.walker.WalkerHistory;
import org.hibernate.collection.spi.PersistentBag;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@Getter
@NoArgsConstructor
@Entity
@Table(schema = "DUNGEON_WALKER_HISTORY", name = "WALKER")
public class WalkerMapper implements Walker {

    public WalkerMapper(
            final Long id,
            final String systemId,
            final List<WalkerHistoryMapper> walkerHistory
    ) {
        this.id = id;
        this.systemId = systemId;
        setWalkerHistory(walkerHistory);
    }

    @Setter
    @Id
    @Column(name = "WALKER_ID")
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "walker_seq"
    )
    @SequenceGenerator(
            name = "walker_seq",
            schema = "DUNGEON_WALKER_HISTORY",
            sequenceName = "WALKER_ID_SEQ"
    )
    private Long id;

    @Setter
    @Column(unique = true)
    private String systemId;

    @OneToMany(
            mappedBy = "walker",
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    private final List<WalkerHistoryMapper> walkerHistory = new ArrayList<>();

    public void setWalkerHistory(final List<WalkerHistoryMapper> walkerHistory) {
        this.walkerHistory.clear();

        if (nonNull(walkerHistory)) {
            walkerHistory.forEach(history -> history.setWalker(this));
            this.walkerHistory.addAll(walkerHistory);
        }
    }

    public void addWalkerHistory(final WalkerHistoryMapper walkerHistory) {
        if (nonNull(walkerHistory)) {
            walkerHistory.setWalker(this);
            this.walkerHistory.add(walkerHistory);
        }
    }

    @Override
    public List<WalkerHistory> getHistory() {
        return new ArrayList<>(walkerHistory);
    }

    @Override
    public String toString() {
        final var part1 = "WalkerMapper(" + id + ", " + systemId + ", ";
        final String part2;
        if (walkerHistory instanceof final PersistentBag<WalkerHistoryMapper> bag) {
            final var dirty = bag.isDirty() ? "dirty" : "clean";
            final var initializing = bag.isInitializing() ? "initializing" : "not initializing";
            final var unreferenced = bag.isUnreferenced() ? "unreferenced" : "referenced";
            part2 = "<lazy loaded: %s %s %s>".formatted(dirty, initializing, unreferenced);
        } else {
            part2 = walkerHistory.toString();
        }

        return part1 + part2 + ")";
    }
}
