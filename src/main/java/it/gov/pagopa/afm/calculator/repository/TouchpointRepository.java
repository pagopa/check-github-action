package src.main.java.it.gov.pagopa.afm.calculator.repository;

import it.gov.pagopa.afm.calculator.entity.Touchpoint;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface TouchpointRepository extends CosmosRepository<Touchpoint, String> {
  Optional<Touchpoint> findByName(String name);
}
