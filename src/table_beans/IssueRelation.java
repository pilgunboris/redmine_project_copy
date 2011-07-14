/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

/**
 *
 * @author root
 */
public class IssueRelation {

    private int id;
    private Integer issue_from_id;
    private Integer issue_to_id;
    private String relation_type;
    private Integer delay;
    ////
    private String t_db;

    public IssueRelation() {
    }

    public Integer getDelay() {
        return delay;
    }

    public int getId() {
        return id;
    }

    public Integer getIssue_from_id() {
        return issue_from_id;
    }

    public Integer getIssue_to_id() {
        return issue_to_id;
    }

    public String getRelation_type() {
        return relation_type;
    }

    public String getT_db() {
        return t_db;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIssue_from_id(Integer issue_from_id) {
        this.issue_from_id = issue_from_id;
    }

    public void setIssue_to_id(Integer issue_to_id) {
        this.issue_to_id = issue_to_id;
    }

    public void setRelation_type(String relation_type) {
        this.relation_type = relation_type;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }
}
