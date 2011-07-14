/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

/**
 *
 * @author root
 */
public class Journal {

    private Integer id;
    private Integer journalized_id;
    private String journalized_type;
    private Integer user_id;
    private String notes;
    private String created_on;
    //------------------
    private String t_db;

    public Journal() {
    }

    public String getCreated_on() {
        return created_on;
    }

    public Integer getId() {
        return id;
    }

    public Integer getJournalized_id() {
        return journalized_id;
    }

    public String getJournalized_type() {
        return journalized_type;
    }

    public String getNotes() {
        return notes;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setJournalized_id(Integer journalized_id) {
        this.journalized_id = journalized_id;
    }

    public void setJournalized_type(String journalized_type) {
        this.journalized_type = journalized_type;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getT_db() {
        return t_db;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }
}
