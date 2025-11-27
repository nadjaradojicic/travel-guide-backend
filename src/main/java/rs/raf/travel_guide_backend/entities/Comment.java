package rs.raf.travel_guide_backend.entities;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class Comment {

    private Integer author_id;
    @NotNull(message = "Content is mandatory!")
    @NotEmpty(message = "Content is mandatory!")
    private String content;

    private String created_at;

    private Integer id;

    private String author_name;

    public Comment() {
    }
    public Comment(Integer id, Integer author_id, String content, String created_at) {
        this.id = id;
        this.author_id = author_id;
        this.content = content;
        this.created_at = created_at;
    }

    public Integer getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(Integer author_id) {
        this.author_id = author_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }
}
