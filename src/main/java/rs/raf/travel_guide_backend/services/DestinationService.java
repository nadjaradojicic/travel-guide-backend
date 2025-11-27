package rs.raf.travel_guide_backend.services;

import rs.raf.travel_guide_backend.entities.Destination;
import rs.raf.travel_guide_backend.respositories.destination.DestinationRepository;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;

public class DestinationService extends AbstractIsAuthorized {

    @Inject
    private DestinationRepository destinationRepository;

    public Response deleteDestination(Integer destination_id) {
        return destinationRepository.deleteDestination(destination_id);
    }

    public Response addDestination(Destination destination) {
        return this.destinationRepository.addDestination(destination);
    }

    public List<Destination> getAllDestinations() {
        return destinationRepository.getAllDestinations();
    }

    public Destination updateDestination(Integer id, Destination destination) {
        return destinationRepository.updateDestination(id, destination);
    }

    public Destination findDestination(Integer destination_id) {
        return destinationRepository.findDestination(destination_id);
    }

}
