/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

/**
 *
 * @author root
 */
public class ChangesetsIssue {

    private Integer changeset_id;
    private Integer issue_id;
    /////
    private String t_db;

    public ChangesetsIssue() {
    }

    public Integer getChangeset_id() {
        return changeset_id;
    }

    public Integer getIssue_id() {
        return issue_id;
    }

    public String getT_db() {
        return t_db;
    }

    public void setChangeset_id(Integer changeset_id) {
        this.changeset_id = changeset_id;
    }

    public void setIssue_id(Integer issue_id) {
        this.issue_id = issue_id;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }
}
