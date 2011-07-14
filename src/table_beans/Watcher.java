/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

/**
 *
 * @author root
 */
public class Watcher {

    private int id;
    private String watchable_type;
    private Integer watchable_id;
    private Integer user_id;
    /////
    private String t_db;

    public Watcher() {
    }

    public int getId() {
        return id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public Integer getWatchable_id() {
        return watchable_id;
    }

    public String getWatchable_type() {
        return watchable_type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public void setWatchable_id(Integer watchable_id) {
        this.watchable_id = watchable_id;
    }

    public void setWatchable_type(String watchable_type) {
        this.watchable_type = watchable_type;
    }

    public String getT_db() {
        return t_db;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }
}
