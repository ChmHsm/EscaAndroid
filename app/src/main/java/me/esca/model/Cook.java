package me.esca.model;

import java.util.Date;
import java.util.Set;

/**
 * Created by Me on 02/06/2017.
 */

public class Cook {

    private Long Id;

    private String username;

    private String password;

    private Set<Recipe> recipes;

    private Image image;

    private Date dateCreated;

    private Date lastUpdated;

    public Long getId() {
        return Id;
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

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Image getImage() {
        return image;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public Cook(String username, String password, Image image) {
        this.username = username;
        this.password = password;
        this.image = image;
    }

    public Cook(Long id, String username, String password, Set<Recipe> recipes, Image image, Date dateCreated, Date lastUpdated) {
        Id = id;
        this.username = username;
        this.password = password;
        this.recipes = recipes;
        this.image = image;
        this.dateCreated = dateCreated;
        this.lastUpdated = lastUpdated;
    }
}
