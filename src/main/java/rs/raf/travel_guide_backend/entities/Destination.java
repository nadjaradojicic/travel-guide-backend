package rs.raf.travel_guide_backend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class Destination {

    @NotNull(message = "Name is mandatory!")
    @NotEmpty(message = "Name is mandatory!")
    private String name;

    private String description;

    private Integer id;

    public Destination(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @JsonProperty("cover")
    private String cover;

    public Destination() {
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
