package me.esca.utils.searchViewUtils.data;

/**
 * Created by Me on 21/07/2017.
 */

public class SearchResultsEntity {

    private Long id;
    private String headerContent;
    private String DescriptionContent;
    private String cookName;
    //Entity reference type (1: Recipe, 2: Cook)
    private int entityType;


    public SearchResultsEntity(Long id, String headerContent, String descriptionContent, int entityType, String cookName) {
        this.id = id;
        this.headerContent = headerContent;
        this.DescriptionContent = descriptionContent;
        this.entityType = entityType;
        this.cookName = cookName;

    }

    public SearchResultsEntity() {
    }

    public String getCookName() {
        return cookName;
    }

    public void setCookName(String cookName) {
        this.cookName = cookName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHeaderContent() {
        return headerContent;
    }

    public void setHeaderContent(String headerContent) {
        this.headerContent = headerContent;
    }

    public String getDescriptionContent() {
        return DescriptionContent;
    }

    public void setDescriptionContent(String descriptionContent) {
        DescriptionContent = descriptionContent;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }
}
