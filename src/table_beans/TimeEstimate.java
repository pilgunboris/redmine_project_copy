/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

/**
 *
 * @author root
 */
public class TimeEstimate {

    private int id;
    private String estimation;
    private Float value;
    private String created_at;
    private String updated_at;
//-----------------------
    private String t_db;

    public TimeEstimate() {
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setEstimation(String estimation) {
        this.estimation = estimation;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getEstimation() {
        return estimation;
    }

    public int getId() {
        return id;
    }

    public String getT_db() {
        return t_db;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public Float getValue() {
        return value;
    }
}
