package rs.raf.travel_guide_backend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class Post {


    @NotNull(message = "Title is required!")
    @NotEmpty(message = "Title is required!")
    private String title;
    @NotNull(message = "Content is required!")
    @NotEmpty(message = "Content is required!")
    private String content;

    private int author_id;

    private List<Comment> comments;

    private String created_at;

    private Integer id;

    private List<Activity> activities;

    private Integer destination_id;

    private String author_name;

    @JsonProperty("cover")
    private String cover;
    public Post() {
    }

    public Post(Integer id, String title, String content, Integer author_id, String created_at, Integer destination_id) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author_id = author_id;
        this.created_at = created_at;
        this.destination_id = destination_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public void setDestination_id(Integer destination_id) {
        this.destination_id = destination_id;
    }

    public Integer getDestination_id() {
        return destination_id;
    }


    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
