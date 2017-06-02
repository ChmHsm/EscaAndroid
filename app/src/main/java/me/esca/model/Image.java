package me.esca.model;

import java.util.Date;

/**
 * Created by Me on 02/06/2017.
 */

public class Image {

    private Long id;

    private String originalName;

    private String originalPath;

    private Date dateCreated;

    private Date lastUpdated;

    private boolean isMainPicture;

    private Cook cook;

    private Recipe recipe;

    public Image(Long id, String originalName, String originalPath, Date dateCreated, Date lastUpdated, boolean isMainPicture, Cook cook, Recipe recipe) {
        this.id = id;
        this.originalName = originalName;
        this.originalPath = originalPath;
        this.dateCreated = dateCreated;
        this.lastUpdated = lastUpdated;
        this.isMainPicture = isMainPicture;
        this.cook = cook;
        this.recipe = recipe;
    }

    public Image(String originalName, String originalPath, boolean isMainPicture, Recipe recipe) {
        this.originalName = originalName;
        this.originalPath = originalPath;
        this.isMainPicture = isMainPicture;
        this.recipe = recipe;
    }

    public Image(String originalName, String originalPath, boolean isMainPicture, Cook cook) {

        this.originalName = originalName;
        this.originalPath = originalPath;
        this.isMainPicture = isMainPicture;
        this.cook = cook;
    }

    public void setOriginalName(String originalName) {

        this.originalName = originalName;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setMainPicture(boolean mainPicture) {
        isMainPicture = mainPicture;
    }

    public void setCook(Cook cook) {
        this.cook = cook;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Long getId() {
        return id;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public boolean isMainPicture() {
        return isMainPicture;
    }

    public Cook getCook() {
        return cook;
    }

    public Recipe getRecipe() {
        return recipe;
    }
}
