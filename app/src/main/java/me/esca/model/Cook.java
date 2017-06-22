package me.esca.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;

/**
 * Created by Me on 02/06/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cook {

    private Long id;

    private String username;

    private String password;

    private Set<Recipe> recipes;

    private Image image;

    private String dateCreated;

    private String lastUpdated;

    public Cook() {
        //Jackson converter
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Set<Recipe> getRecipes() {
        return recipes;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRecipes(Set<Recipe> recipes) {
        this.recipes = recipes;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Image getImage() {
        return image;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public Cook(String username, String password, Image image) {
        this.username = username;
        this.password = password;
        this.image = image;
    }

    public Cook(Long id, String username, String password, Set<Recipe> recipes, Image image, String dateCreated, String lastUpdated) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.recipes = recipes;
        this.image = image;
        this.dateCreated = dateCreated;
        this.lastUpdated = lastUpdated;
    }
}
