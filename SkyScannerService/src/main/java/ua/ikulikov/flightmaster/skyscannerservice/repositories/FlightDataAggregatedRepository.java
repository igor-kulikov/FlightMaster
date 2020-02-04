package ua.ikulikov.flightmaster.skyscannerservice.repositories;

import org.springframework.stereotype.Repository;
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightDataAggregated;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class FlightDataAggregatedRepository implements IFlightDataAggregatedRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<FlightDataAggregated> showPollResults(Long flightRequestId) {
        Query query = entityManager.createQuery("select a from FlightDataAggregated a where a.extId =: extId", FlightDataAggregated.class);
        query.setParameter("extId", flightRequestId);
        return query.getResultList();
    }
}
