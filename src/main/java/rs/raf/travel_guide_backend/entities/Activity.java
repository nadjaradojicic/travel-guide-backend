package rs.raf.travel_guide_backend.entities;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class Activity {

    @NotNull(message = "Name is mandatory!")
    @NotEmpty(message = "Name is mandatory!")
    private String name;

    private Integer id;

    public Activity() {
    }

    public Activity(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
