package ua.ikulikov.flightmaster.skyscannerservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightDataAggregated;
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightRequestPollDto;
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightRequestPollStatus;
import ua.ikulikov.flightmaster.skyscannerservice.entities.flightdata.FlightData;
import ua.ikulikov.flightmaster.skyscannerservice.repositories.IAgentRepository;
import ua.ikulikov.flightmaster.skyscannerservice.repositories.ICarrierRepository;
import ua.ikulikov.flightmaster.skyscannerservice.repositories.IFlightDataAggregatedRepository;
import ua.ikulikov.flightmaster.skyscannerservice.repositories.IFlightRequestPollRepository;
import ua.ikulikov.flightmaster.skyscannerservice.repositories.IItineraryRepository;
import ua.ikulikov.flightmaster.skyscannerservice.repositories.ILegRepository;
import ua.ikulikov.flightmaster.skyscannerservice.repositories.IPlaceRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DatabaseService implements IDatabaseService {
    private final IAgentRepository agentRepository;
    private final ICarrierRepository carrierRepository;
    private final IPlaceRepository placeRepository;
    private final ILegRepository legRepository;
    private final IItineraryRepository itineraryRepository;
    private final IFlightRequestPollRepository flightRequestPollRepository;
    private final IFlightDataAggregatedRepository repository;

    @Override
    public void persistDicts(FlightData flightData) {
        persistNewData(flightData.getAgents(), agentRepository);
        persistNewData(flightData.getCarriers(), carrierRepository);
        persistNewData(flightData.getPlaces(), placeRepository);
        persistNewData(flightData.getLegs(), legRepository);
        persistNewData(flightData.getItineraries(), itineraryRepository);
    }

    @Override
    public FlightRequestPollDto persistPoll(FlightRequestPollDto poll, FlightRequestPollStatus status, LocalDateTime dt) {
        poll.setFlightRequestPollStatus(status);
        poll.setPollStatusDateTime(dt);
        return flightRequestPollRepository.saveAndFlush(poll);
    }

    @Override
    public FlightRequestPollDto persistPoll(FlightRequestPollDto poll, FlightRequestPollStatus status) {
        return persistPoll(poll, status, LocalDateTime.now());
    }

    @Override
    public List<FlightDataAggregated> showPollResults(Long flightRequestId) {
        return repository.showPollResults(flightRequestId);
    }

    private synchronized <T> void persistNewData(Collection<T> data, JpaRepository<T, ?> repository) {
        Set<T> newData = new HashSet<>(data);
        newData.removeAll(repository.findAll());
        repository.saveAll(newData);
    }
}
