/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

/**
 *
 * @author root
 */
public class Document {

    private int id;
    private Integer project_id;
    private Integer category_id;
    private String title;
    private String description;
    private String created_on;
    /////
    private String t_db;

    public Document() {
    }

    public Integer getCategory_id() {
        return category_id;
    }

    public String getCreated_on() {
        return created_on;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Integer getProject_id() {
        return project_id;
    }

    public String getTitle() {
        return title;
    }

    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getT_db() {
        return t_db;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }
}
