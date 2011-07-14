/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

/**
 *
 * @author root
 */
public class Message {

    private int id;
    private Integer board_id;
    private Integer parent_id;
    private String subject;
    private String content;
    private Integer author_id;
    private Integer replies_count;
    private Integer last_reply_id;
    private String created_on;
    private String updated_on;
    /////
    private String t_db;

    public Message() {
    }

    public Integer getAuthor_id() {
        return author_id;
    }

    public Integer getBoard_id() {
        return board_id;
    }

    public String getContent() {
        return content;
    }

    public String getCreated_on() {
        return created_on;
    }

    public int getId() {
        return id;
    }

    public Integer getLast_reply_id() {
        return last_reply_id;
    }

    public Integer getParent_id() {
        return parent_id;
    }

    public Integer getReplies_count() {
        return replies_count;
    }

    public String getSubject() {
        return subject;
    }

    public String getT_db() {
        return t_db;
    }

    public String getUpdated_on() {
        return updated_on;
    }

    public void setAuthor_id(Integer author_id) {
        this.author_id = author_id;
    }

    public void setBoard_id(Integer board_id) {
        this.board_id = board_id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLast_reply_id(Integer last_reply_id) {
        this.last_reply_id = last_reply_id;
    }

    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public void setReplies_count(Integer replies_count) {
        this.replies_count = replies_count;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }

    public void setUpdated_on(String updated_on) {
        this.updated_on = updated_on;
    }
}
