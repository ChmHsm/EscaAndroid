package me.esca.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by Me on 02/06/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipe implements Serializable {

    private Long id;

    private String title;

    private int difficultyRating;

    private int prepTime;

    private double prepCost;

    private String ingredients;

    private String instructions;

    private String dateCreated;

    private String lastUpdated;

    private Cook cook;

    public Recipe(String title, int difficultyRating, int prepTime, double prepCost, String ingredients, String instructions, String dateCreated, String lastUpdated, Cook cook, Set<Image> recipeImage) {
        this.title = title;
        this.difficultyRating = difficultyRating;
        this.prepTime = prepTime;
        this.prepCost = prepCost;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.dateCreated = dateCreated;
        this.lastUpdated = lastUpdated;
        this.cook = cook;
        this.recipeImage = recipeImage;
    }

    public Recipe(Long id, String title, int difficultyRating, int prepTime, double prepCost, String ingredients, String instructions, Cook cook, Set<Image> recipeImage) {

        this.id = id;
        this.title = title;
        this.difficultyRating = difficultyRating;
        this.prepTime = prepTime;
        this.prepCost = prepCost;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.cook = cook;
        this.recipeImage = recipeImage;
    }

    public Recipe(String title, int difficultyRating, int prepTime, double prepCost, String ingredients, String instructions) {

        this.title = title;
        this.difficultyRating = difficultyRating;
        this.prepTime = prepTime;
        this.prepCost = prepCost;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public Recipe() {
        //Jackson converter
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public void setDifficultyRating(int difficultyRating) {
        this.difficultyRating = difficultyRating;
    }

    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
    }

    public void setPrepCost(double prepCost) {
        this.prepCost = prepCost;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setCook(Cook cook) {
        this.cook = cook;
    }

    public void setRecipeImage(Set<Image> recipeImage) {
        this.recipeImage = recipeImage;
    }

    public Long getId() {

        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getDifficultyRating() {
        return difficultyRating;
    }

    public int getPrepTime() {
        return prepTime;
    }

    public double getPrepCost() {
        return prepCost;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public Cook getCook() {
        return cook;
    }

    public Set<Image> getRecipeImage() {
        return recipeImage;
    }

    private Set<Image> recipeImage;

    @Override
    public String toString() {
        return "Recipe [id=" + id + "]";
    }
}
