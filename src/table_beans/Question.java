/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

/**
 *
 * @author root
 */
public class Question {

    private int id;
    private Integer journal_id;
    private Integer author_id;
    private Integer assigned_to_id;
    private Boolean opened;
    private Integer issue_id;
    /////
    String t_db;

    public Question() {
    }

    public Integer getAssigned_to_id() {
        return assigned_to_id;
    }

    public Integer getAuthor_id() {
        return author_id;
    }

    public int getId() {
        return id;
    }

    public Integer getIssue_id() {
        return issue_id;
    }

    public Integer getJournal_id() {
        return journal_id;
    }

    public Boolean getOpened() {
        return opened;
    }

    public void setAssigned_to_id(Integer assigned_to_id) {
        this.assigned_to_id = assigned_to_id;
    }

    public void setAuthor_id(Integer author_id) {
        this.author_id = author_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIssue_id(Integer issue_id) {
        this.issue_id = issue_id;
    }

    public void setJournal_id(Integer journal_id) {
        this.journal_id = journal_id;
    }

    public void setOpened(Boolean opened) {
        this.opened = opened;
    }

    public String getT_db() {
        return t_db;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }
}
