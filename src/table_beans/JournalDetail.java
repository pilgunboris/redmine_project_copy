/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

/**
 *
 * @author root
 */
public class JournalDetail {

    private int id;
    private Integer journal_id;
    private String property;
    private String prop_key;
    private String old_value;
    private String value;
    ///////
    private String t_db;

    public JournalDetail() {
    }

    public int getId() {
        return id;
    }

    public Integer getJournal_id() {
        return journal_id;
    }

    public String getOld_value() {
        return old_value;
    }

    public String getProp_key() {
        return prop_key;
    }

    public String getProperty() {
        return property;
    }

    public String getT_db() {
        return t_db;
    }

    public String getValue() {
        return value;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setJournal_id(Integer journal_id) {
        this.journal_id = journal_id;
    }

    public void setOld_value(String old_value) {
        this.old_value = old_value;
    }

    public void setProp_key(String prop_key) {
        this.prop_key = prop_key;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
