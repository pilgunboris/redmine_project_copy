/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

/**
 *
 * @author root
 */
public class EnabledModule {

    private int id;
    private Integer project_id;
    private String name;
    ///
    String t_db;

    public EnabledModule() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getProject_id() {
        return project_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public String getT_db() {
        return t_db;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }
}
