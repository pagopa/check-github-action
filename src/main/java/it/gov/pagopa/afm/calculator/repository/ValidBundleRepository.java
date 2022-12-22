package src.main.java.it.gov.pagopa.afm.calculator.repository;

import it.gov.pagopa.afm.calculator.entity.ValidBundle;
import org.springframework.stereotype.Repository;

@Repository
public interface ValidBundleRepository extends CosmosRepository<ValidBundle, String> {
}
