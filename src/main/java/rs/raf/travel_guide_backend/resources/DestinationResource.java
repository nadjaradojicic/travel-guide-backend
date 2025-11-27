package rs.raf.travel_guide_backend.resources;

import rs.raf.travel_guide_backend.entities.Destination;
import rs.raf.travel_guide_backend.services.DestinationService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/destinations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DestinationResource {

    @Inject
    private DestinationService destinationService;

    @DELETE
    @Path("/{id}")
    public Response deleteDestination(@PathParam("id") Integer id) {
        return destinationService.deleteDestination(id);
    }

    @POST
    public Response addDestination(@Valid Destination destination) {
        return destinationService.addDestination(destination);
    }

    @GET
    public Response getAllDestinations() {
        return Response.ok(this.destinationService.getAllDestinations()).build();
    }

    @GET
    @Path("/{id}")
    public Destination findDestination(@PathParam("id") Integer destination_id) {
        return destinationService.findDestination(destination_id);
    }

    @PUT
    @Path("/{id}")
    public Destination updateDestination(@PathParam("id") Integer id, Destination destination) {
        return destinationService.updateDestination(id, destination);
    }
}
