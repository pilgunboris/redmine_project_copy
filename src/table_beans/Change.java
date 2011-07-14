/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

/**
 *
 * @author root
 */
public class Change {

    private int id;
    private Integer changeset_id;
    private String action;
    private String path;
    private String from_path;
    private String from_revision;
    private String revision;
    private String branch;
    ////
    private String t_db;

    public Change() {
    }

    public String getAction() {
        return action;
    }

    public String getBranch() {
        return branch;
    }

    public Integer getChangeset_id() {
        return changeset_id;
    }

    public String getFrom_path() {
        return from_path;
    }

    public String getFrom_revision() {
        return from_revision;
    }

    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getRevision() {
        return revision;
    }

    public String getT_db() {
        return t_db;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setChangeset_id(Integer changeset_id) {
        this.changeset_id = changeset_id;
    }

    public void setFrom_path(String from_path) {
        this.from_path = from_path;
    }

    public void setFrom_revision(String from_revision) {
        this.from_revision = from_revision;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }
}
