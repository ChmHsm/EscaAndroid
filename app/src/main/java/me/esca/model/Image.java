package me.esca.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Created by Me on 02/06/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Image {

    private Long id;

    private String originalName;

    private String originalPath;

    private String dateCreated;

    private String lastUpdated;

    private boolean isMainPicture;

    private Cook cook;

    private Recipe recipe;

    public Image(Long id, String originalName, String originalPath, String dateCreated, String lastUpdated, boolean isMainPicture, Cook cook, Recipe recipe) {
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

    public Image() {
        //Jackson converter
    }

    public void setOriginalName(String originalName) {

        this.originalName = originalName;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setLastUpdated(String lastUpdated) {
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

    public String getDateCreated() {
        return dateCreated;
    }

    public String getLastUpdated() {
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
