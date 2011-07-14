/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db_controls;

import table_beans.RedmineEnum;

/**
 *
 * @author root
 */
public class ParamHelper {

    private Object db;
    private String s_db;
    private String t_db;
    private int id;
    private String type;
    private RedmineEnum re;
    private String tableName;
    //for update messages
    private Integer last_reply_id;
    private Integer parent_id;

    public ParamHelper(Object db, int id, Integer last_reply_id, Integer parent_id) {
        this.db = db;
        this.id = id;
        this.last_reply_id = last_reply_id;
        this.parent_id = parent_id;
    }

    ////////
    public ParamHelper(String s_db, String t_db, int id) {
        this.s_db = s_db;
        this.t_db = t_db;
        this.id = id;
    }

    public ParamHelper(String s_db, String t_db, String type) {
        this.s_db = s_db;
        this.t_db = t_db;
        this.type = type;
        AppSqlConfig inst = AppSqlConfig.getInstance();
    }

    public ParamHelper(Object db, int id, String type) {
        this.db = db;
        this.id = id;
        this.type = type;
    }

    /**
     * ids = GetEnum
     *
     * @param db
     * @param type
     * //for select all
     */
    public ParamHelper(Object db, String type) {
        this.db = db;
        this.type = type;
    }

    public ParamHelper(Object db, int id) {
        this.db = db;
        this.id = id;
    }

    public ParamHelper(String t_db, RedmineEnum redmineEnum) {
        this.t_db = t_db;
        this.re = redmineEnum;
    }

    public Object getDb() {
        return db;
    }

    public int getId() {
        return id;
    }

    public RedmineEnum getRedmineEnum() {
        return re;
    }

    public String getS_db() {
        return s_db;
    }

    public String getT_db() {
        return t_db;
    }

    public String getType() {
        return type;
    }

    public void setDb(Object db) {
        this.db = db;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRedmineEnum(RedmineEnum redmineEnum) {
        this.re = redmineEnum;
    }

    public void setS_db(String s_db) {
        this.s_db = s_db;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getLast_reply_id() {
        return last_reply_id;
    }

    public Integer getParent_id() {
        return parent_id;
    }

    public RedmineEnum getRe() {
        return re;
    }

    public String getTableName() {
        return tableName;
    }

    public void setLast_reply_id(Integer last_reply_id) {
        this.last_reply_id = last_reply_id;
    }

    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public void setRe(RedmineEnum re) {
        this.re = re;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
