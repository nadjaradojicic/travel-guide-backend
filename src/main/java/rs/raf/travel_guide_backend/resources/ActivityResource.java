package rs.raf.travel_guide_backend.resources;

import rs.raf.travel_guide_backend.entities.Activity;
import rs.raf.travel_guide_backend.services.ActivityService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/activities")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ActivityResource {

    @Inject
    private ActivityService activityService;

    @GET
    public Response getAllActivities() {
        return Response.ok(this.activityService.getAllActivities()).build();
    }

    @POST
    public Response addActivity(@Valid Activity activity) {
        this.activityService.addActivity(activity);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("/{id}")
    public Activity findActivity(@PathParam("id") Integer activity_id) {
        return activityService.findActivity(activity_id);
    }

    @DELETE
    @Path("/{id}")
    public Response deleteActivity(@PathParam("id") Integer activity_id) {
        activityService.deleteActivity(activity_id);
        return Response.noContent().build();
    }
}
