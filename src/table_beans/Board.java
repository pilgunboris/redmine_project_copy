/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

/**
 *
 * @author root
 */
public class Board {

    private int id;
    private Integer project_id;
    private String name;
    private String description;
    private Integer position;
    private Integer topics_count;
    private Integer messages_count;
    /////
    private String t_db;

    public Board() {
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Integer getMessages_count() {
        return messages_count;
    }

    public String getName() {
        return name;
    }

    public Integer getPosition() {
        return position;
    }

    public Integer getProject_id() {
        return project_id;
    }

    public Integer getTopics_count() {
        return topics_count;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMessages_count(Integer messages_count) {
        this.messages_count = messages_count;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public void setTopics_count(Integer topics_count) {
        this.topics_count = topics_count;
    }

    public String getT_db() {
        return t_db;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }
}
