/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

/**
 *
 * @author root
 */
public class TimeEntry {

    private int id;
    private Integer project_id;
    private Integer user_id;
    private Integer issue_id;
    private Double hours;
    private String comments;
    private Integer activity_id;
    private String spent_on;
    private Integer tyear;
    private Integer tmonth;
    private Integer tweek;
    private String created_on;
    private String updated_on;
    /////
    private String t_db;

    public TimeEntry() {
    }

    public Integer getActivity_id() {
        return activity_id;
    }

    public String getComments() {
        return comments;
    }

    public String getCreated_on() {
        return created_on;
    }

    public Double getHours() {
        return hours;
    }

    public int getId() {
        return id;
    }

    public Integer getIssue_id() {
        return issue_id;
    }

    public Integer getProject_id() {
        return project_id;
    }

    public String getSpent_on() {
        return spent_on;
    }

    public String getT_db() {
        return t_db;
    }

    public Integer getTmonth() {
        return tmonth;
    }

    public Integer getTweek() {
        return tweek;
    }

    public Integer getTyear() {
        return tyear;
    }

    public String getUpdated_on() {
        return updated_on;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setActivity_id(Integer activity_id) {
        this.activity_id = activity_id;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIssue_id(Integer issue_id) {
        this.issue_id = issue_id;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public void setSpent_on(String spent_on) {
        this.spent_on = spent_on;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }

    public void setTmonth(Integer tmonth) {
        this.tmonth = tmonth;
    }

    public void setTweek(Integer tweek) {
        this.tweek = tweek;
    }

    public void setTyear(Integer tyear) {
        this.tyear = tyear;
    }

    public void setUpdated_on(String updated_on) {
        this.updated_on = updated_on;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
}
