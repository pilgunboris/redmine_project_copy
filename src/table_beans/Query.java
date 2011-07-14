/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

/**
 *
 * @author root
 */
public class Query {

    private int id;
    private Integer project_id;
    private String name;
    private String filters;
    private Integer user_id;
    private Boolean is_public;
    private String column_names;
    private String sort_criteria;
    private String group_by;
    //////
    private String t_db;

    public Query() {
    }

    public String getColumn_names() {
        return column_names;
    }

    public String getFilters() {
        return filters;
    }

    public String getGroup_by() {
        return group_by;
    }

    public int getId() {
        return id;
    }

    public Boolean getIs_public() {
        return is_public;
    }

    public String getName() {
        return name;
    }

    public Integer getProject_id() {
        return project_id;
    }

    public String getSort_criteria() {
        return sort_criteria;
    }

    public String getT_db() {
        return t_db;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setColumn_names(String column_names) {
        this.column_names = column_names;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public void setGroup_by(String group_by) {
        this.group_by = group_by;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIs_public(Boolean is_public) {
        this.is_public = is_public;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public void setSort_criteria(String sort_criteria) {
        this.sort_criteria = sort_criteria;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
}
