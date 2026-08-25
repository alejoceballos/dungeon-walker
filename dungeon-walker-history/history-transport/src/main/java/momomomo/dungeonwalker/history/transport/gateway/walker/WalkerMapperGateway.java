package momomomo.dungeonwalker.history.transport.gateway.walker;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import momomomo.dungeonwalker.history.domain.model.History;
import momomomo.dungeonwalker.history.transport.gateway.SaveGateway;
import momomomo.dungeonwalker.history.transport.gateway.SelectableGateway;
import momomomo.dungeonwalker.history.transport.mapper.walker.WalkerMapper;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class WalkerMapperGateway implements SaveGateway<WalkerMapper>, SelectableGateway {

    @PersistenceContext
    private final EntityManager em;

    private final WalkerJpaRepository walkerJpaRepository;

    @Override
    public boolean isResponsibleFor(@NonNull final History history) {
        return history instanceof WalkerMapper;
    }

    @Transactional
    @Override
    public WalkerMapper save(final WalkerMapper walker) {
        final var id = findIdBySystemId(walker.getSystemId());

        if (isNull(id)) {
            return walkerJpaRepository.save(walker);
        }

        walker.setId(id);
        return em.merge(walker);
    }

    // TODO: Put a cache here
    private Long findIdBySystemId(final String systemId) {
        return walkerJpaRepository.findIdBySystemId(systemId);
    }

}
