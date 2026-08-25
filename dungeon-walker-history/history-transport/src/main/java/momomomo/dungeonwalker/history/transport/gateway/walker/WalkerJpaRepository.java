package momomomo.dungeonwalker.history.transport.gateway.walker;

import momomomo.dungeonwalker.history.transport.mapper.walker.WalkerMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WalkerJpaRepository extends JpaRepository<WalkerMapper, Long> {

    @Query("SELECT w.id FROM WalkerMapper w WHERE w.systemId = :systemId")
    Long findIdBySystemId(String systemId);

    WalkerMapper findBySystemId(String systemId);

}
