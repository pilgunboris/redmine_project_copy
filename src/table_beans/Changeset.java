/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

/**
 *
 * @author root
 */
public class Changeset {

    private int id;
    private Integer repository_id;
    private String revision;
    private String committer;
    private String committed_on;
    private String comments;
    private String commit_date;
    private String scmid;
    private Integer user_id;
    /////
    private String t_db;

    public Changeset() {
    }

    public String getComments() {
        return comments;
    }

    public String getCommit_date() {
        return commit_date;
    }

    public String getCommitter() {
        return committer;
    }

    public String getCommitted_on() {
        return committed_on;
    }

    public int getId() {
        return id;
    }

    public Integer getRepository_id() {
        return repository_id;
    }

    public String getRevision() {
        return revision;
    }

    public String getScmid() {
        return scmid;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setCommit_date(String commit_date) {
        this.commit_date = commit_date;
    }

    public void setCommitter(String committer) {
        this.committer = committer;
    }

    public void setCommitted_on(String committed_on) {
        this.committed_on = committed_on;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRepository_id(Integer repository_id) {
        this.repository_id = repository_id;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public void setScmid(String scmid) {
        this.scmid = scmid;
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
