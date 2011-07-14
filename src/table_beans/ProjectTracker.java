/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

/**
 *
 * @author root
 */
public class ProjectTracker {

    private Integer project_id;
    private Integer tracker_id;
    /////
    private String t_db;

    public ProjectTracker() {
    }

    public Integer getProject_id() {
        return project_id;
    }

    public Integer getTracker_id() {
        return tracker_id;
    }

    public String getT_db() {
        return t_db;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }

    public void setTracker_id(Integer tracker_id) {
        this.tracker_id = tracker_id;
    }
}
