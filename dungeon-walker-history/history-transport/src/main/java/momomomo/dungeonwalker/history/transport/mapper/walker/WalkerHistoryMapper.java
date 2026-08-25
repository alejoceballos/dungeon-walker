package momomomo.dungeonwalker.history.transport.mapper.walker;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import momomomo.dungeonwalker.history.domain.model.walker.WalkerHistory;
import momomomo.dungeonwalker.history.transport.mapper.map.CoordinatesMapper;

import java.time.Instant;
import java.time.OffsetDateTime;

import static jakarta.persistence.FetchType.LAZY;

@ToString(
        includeFieldNames = false,
        doNotUseGetters = true,
        exclude = "walker"
)
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "DUNGEON_WALKER_HISTORY", name = "WALKER_HISTORY")
public class WalkerHistoryMapper implements WalkerHistory {

    @Getter
    @Id
    @Column(name = "WALKER_HISTORY_ID")
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "walker_history_seq"
    )
    @SequenceGenerator(
            name = "walker_history_seq",
            schema = "DUNGEON_WALKER_HISTORY",
            sequenceName = "WALKER_HISTORY_ID_SEQ"
    )
    private Long id;

    @Column(name = "HISTORY_TIMESTAMP", nullable = false)
    private OffsetDateTime timestamp;

    @Getter
    @Embedded
    private CoordinatesMapper coordinates;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "WALKER_ID", nullable = false)
    private WalkerMapper walker;


    @Override
    public Instant getTimestamp() {
        return timestamp.toInstant();
    }
}
