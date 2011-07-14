/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

/**
 *
 * @author root
 */
public class UserPref {

    private int id;
    private Integer user_id;
    private String others;
    private Boolean hide_mail;
    private String time_zone;
    ///
    private String t_db;

    public UserPref() {
    }

    public Boolean getHide_mail() {
        return hide_mail;
    }

    public int getId() {
        return id;
    }

    public String getOthers() {
        return others;
    }

    public String getT_db() {
        return t_db;
    }

    public String getTime_zone() {
        return time_zone;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setHide_mail(Boolean hide_mail) {
        this.hide_mail = hide_mail;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }

    public void setTime_zone(String time_zone) {
        this.time_zone = time_zone;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
}
