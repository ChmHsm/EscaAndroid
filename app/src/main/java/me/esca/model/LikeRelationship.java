package me.esca.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Me on 31/07/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LikeRelationship implements Serializable {

    private Long id;

    private Recipe recipe;

    private Cook cook;

    public Long getId() {
        return id;
    }

    public LikeRelationship() {
        //Jackson
    }

    public LikeRelationship(Long id, Cook cook) {

        this.id = id;
        this.cook = cook;
    }

    public Recipe getRecipe() {

        return recipe;

    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Cook getCook() {
        return cook;
    }

    public void setCook(Cook cook) {
        this.cook = cook;
    }

    public LikeRelationship(Long id, Recipe recipe, Cook cook) {
        this.id = id;
        this.recipe = recipe;
        this.cook = cook;
    }

    @Override
    public String toString() {
        return "LikeRelationship [id=" + id + "]";
    }
}
