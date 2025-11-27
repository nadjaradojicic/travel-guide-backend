package rs.raf.travel_guide_backend.respositories.destination;

import rs.raf.travel_guide_backend.entities.Destination;

import javax.ws.rs.core.Response;
import java.util.List;

public interface DestinationRepository {

    List<Destination> getAllDestinations();

    Destination findDestination(Integer destination_id);

    Destination updateDestination(Integer id, Destination destination);

    Response addDestination(Destination destination);

    Response deleteDestination(Integer destination_id);
}
