/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

/**
 *
 * @author root
 */
public class Token {

    private int id;
    private Integer user_id;
    private String action;
    private String value;
    private String created_on;
    ////
    private String t_db;

    public Token() {
    }

    public String getAction() {
        return action;
    }

    public String getCreated_on() {
        return created_on;
    }

    public int getId() {
        return id;
    }

    public String getT_db() {
        return t_db;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public String getValue() {
        return value;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
