/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *
 */
package table_beans;

/**
 *
 * @author root
 * managed by IssueControl
 * 
 */
public class Milestone {

    private int id;
    private String target;
    private String deadline;
    private Integer project_id;
    //-----------------------
    private String t_db;

    public Milestone() {
    }

    public String getDeadline() {
        return deadline;
    }

    public int getId() {
        return id;
    }

    public Integer getProject_id() {
        return project_id;
    }

    public String getT_db() {
        return t_db;
    }

    public String getTarget() {
        return target;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
