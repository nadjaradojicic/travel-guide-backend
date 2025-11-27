package rs.raf.travel_guide_backend.respositories.activity;

import rs.raf.travel_guide_backend.entities.Activity;

import javax.swing.*;
import java.util.List;

public interface ActivityRepository {

    List<Activity> getAllActivities();

    Activity findActivityById(Integer activity_id);

    List<Activity> findActivitiesForPost(Integer postId);

    void addActivity(Activity activity);

    void deleteActivity(Integer activity_id);

}
