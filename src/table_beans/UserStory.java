/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

/**
 *
 * @author root
 */
public class UserStory {

    private int id;
    private String name;
    private String description;
    private Integer project_id;
    private Integer time_estimate_id;
    private String created_at;
    private String updated_at;
    private Integer priority;
    private Integer us_number;
    private Integer milestone_id;
    private Integer version_id;
    //--------------------
    private String t_db;

    public UserStory() {
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMilestone_id(Integer milestone_id) {
        this.milestone_id = milestone_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }

    public void setTime_estimate_id(Integer time_estimate_id) {
        this.time_estimate_id = time_estimate_id;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public void setUs_number(Integer us_number) {
        this.us_number = us_number;
    }

    public void setVersion_id(Integer version_id) {
        this.version_id = version_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Integer getMilestone_id() {
        return milestone_id;
    }

    public String getName() {
        return name;
    }

    public Integer getPriority() {
        return priority;
    }

    public Integer getProject_id() {
        return project_id;
    }

    public String getT_db() {
        return t_db;
    }

    public Integer getTime_estimate_id() {
        return time_estimate_id;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public Integer getUs_number() {
        return us_number;
    }

    public Integer getVersion_id() {
        return version_id;
    }
}
