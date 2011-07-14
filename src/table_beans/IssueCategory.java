/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

/**
 *
 * @author root
 */
public class IssueCategory {

    private int id;
    private int project_id;
    private String name;
    private Integer assigned_to_id;
    private String t_db;

    public IssueCategory() {
    }

    public Integer getAssigned_to_id() {
        return assigned_to_id;
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

    public String getT_db() {
        return t_db;
    }

    public void setAssigned_to_id(Integer assigned_to_id) {
        this.assigned_to_id = assigned_to_id;
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

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }
}
