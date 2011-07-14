/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author root
 */
public class RedmineEnum {

    private int id;
    private String name;
    private Integer position;
    private boolean is_default;
    private String type;
    private boolean active;
    private Integer project_id;
    private Integer parent_id;
    private String t_db = null;

    public RedmineEnum() {
    }

    public String asString() {
        String res = "";
        res += "\nid = " + id
                + "\nname = " + name
                + "\nposition = " + position
                + "\nis_default = " + is_default
                + "\ntype = " + type
                + "\nactive = " + active
                + "\nproject_id = " + project_id
                + "\nparent_id = " + parent_id;
        return res;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isIs_default() {
        return is_default;
    }

    public String getName() throws Exception {
        return name;
    }

    public Integer getParent_id() {
        return parent_id;
    }

    public Integer getPosition() {
        return position;
    }

    public Integer getProject_id() {
        return project_id;
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setIs_default(boolean is_default) {
        this.is_default = is_default;
    }

    public void setName(String name) throws UnsupportedEncodingException {
        this.name =new String(name.getBytes("utf8"));
    }

    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getT_db() {
        return t_db;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }
}
