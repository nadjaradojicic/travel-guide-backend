package rs.raf.travel_guide_backend.entities;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class User {

    @Email
    @NotNull(message = "Email is mandatory!")
    @NotEmpty(message = "Email is mandatory!")
    private String email;

    @NotNull(message = "Name is mandatory!")
    @NotEmpty(message = "Name is mandatory!!")
    private String name;

    @NotNull(message = "Surname is mandatory!")
    @NotEmpty(message = "Surname is mandatory!")
    private String surname;

    private Role role;

    @NotNull(message = "Password is mandatory!")
    @NotEmpty(message = "Password is mandatory!")
    private String hashedPassword;

    private Integer id;

    private Boolean status;

    public User() {
    }

    public User(String email, String name, String surname, Role role, String hashedPassword, Integer id, boolean status) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.hashedPassword = hashedPassword;
        this.id = id;
        this.status = status;
    }

    public User(Integer id, String name, String surname, String email, Role role, Boolean status, String hashedPassword) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.role = role;
        this.status = status;
        this.hashedPassword = hashedPassword;
    }

    public User(Integer id, String name, String surname, String email, Role role, Boolean status) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
