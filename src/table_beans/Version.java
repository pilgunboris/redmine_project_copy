/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

/**
 *
 * @author root
 */
public class Version {

    private int id;
    private int project_id;
    private String name;
    private String description;
    private String effective_date;
    private String created_on;
    private String updated_on;
    private String wiki_page_title;
    private String status;
    private String sharing;
    private Integer duration;
    private String t_db;

    public Version() {
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setEffective_date(String effective_date) {
        this.effective_date = effective_date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProject_id(int project_id) {
        this.project_id = project_id;
    }

    public void setSharing(String sharing) {
        this.sharing = sharing;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }

    public void setUpdated_on(String updated_on) {
        this.updated_on = updated_on;
    }

    public void setWiki_page_title(String wiki_page_title) {
        this.wiki_page_title = wiki_page_title;
    }

    public String getCreated_on() {
        return created_on;
    }

    public String getDescription() {
        return description;
    }

    public Integer getDuration() {
        return duration;
    }

    public String getEffective_date() {
        return effective_date;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getProject_id() {
        return project_id;
    }

    public String getSharing() {
        return sharing;
    }

    public String getStatus() {
        return status;
    }

    public String getT_db() {
        return t_db;
    }

    public String getUpdated_on() {
        return updated_on;
    }

    public String getWiki_page_title() {
        return wiki_page_title;
    }
}
