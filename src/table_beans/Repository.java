/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

/**
 *
 * @author root
 */
public class Repository {

    private int id;
    private Integer project_id;
    private String url;
    private String login;
    private String password;
    private String root_url;
    private String type;
    ////
    private String t_db;

    public Repository() {
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Integer getProject_id() {
        return project_id;
    }

    public String getT_db() {
        return t_db;
    }

    public String getRoot_url() {
        return root_url;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }

    public void setRoot_url(String root_url) {
        this.root_url = root_url;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
