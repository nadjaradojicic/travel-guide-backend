package rs.raf.travel_guide_backend.services;

import rs.raf.travel_guide_backend.entities.Activity;
import rs.raf.travel_guide_backend.respositories.activity.ActivityRepository;

import javax.inject.Inject;
import java.util.List;

public class ActivityService extends AbstractIsAuthorized {

    @Inject
    private ActivityRepository activityRepository;

    public Activity findActivity(Integer activity_id) {
        return activityRepository.findActivityById(activity_id);
    }

    public List<Activity> getAllActivities() {
        return activityRepository.getAllActivities();
    }

    public void addActivity(Activity activity) {
        activityRepository.addActivity(activity);
    }

    public void deleteActivity(Integer activity_id) {
        activityRepository.deleteActivity(activity_id);
    }
}
