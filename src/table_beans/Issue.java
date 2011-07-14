/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

/**
 *
 * @author root
 */
public class Issue {

    private int id;
    private int tracker_id;
    private int project_id;
    private String subject;
    private String description;
    private String due_date;
    private Integer category_id;
    private int status_id;
    private Integer assigned_to_id;
    private int priority_id;
    private Integer fixed_version_id;
    private int author_id;
    private int lock_version;
    private String created_on;
    private String updated_on;
    private String start_date;
    private int done_ratio;
    private Float estimated_hours;
    private Integer user_story_id;
    private String redirect_to;
    private Integer parent_id;
    private Integer root_id;
    private Integer lft;
    private Integer rgt;
//---------------------------
    private String t_db;

    public Issue() {
    }

    public Integer getAssigned_to_id() {
        return assigned_to_id;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public Integer getCategory_id() {
        return category_id;
    }

    public String getCreated_on() {
        return created_on;
    }

    public String getDescription() {
        return description;
    }

    public int getDone_ratio() {
        return done_ratio;
    }

    public String getDue_date() {
        return due_date;
    }

    public Float getEstimated_hours() {
        return estimated_hours;
    }

    public Integer getFixed_version_id() {
        return fixed_version_id;
    }

    public int getId() {
        return id;
    }

    public Integer getLft() {
        return lft;
    }

    public int getLock_version() {
        return lock_version;
    }

    public Integer getParent_id() {
        return parent_id;
    }

    public int getPriority_id() {
        return priority_id;
    }

    public int getProject_id() {
        return project_id;
    }

    public String getRedirect_to() {
        return redirect_to;
    }

    public Integer getRgt() {
        return rgt;
    }

    public Integer getRoot_id() {
        return root_id;
    }

    public String getStart_date() {
        return start_date;
    }

    public int getStatus_id() {
        return status_id;
    }

    public String getSubject() {
        return subject;
    }

    public String getT_db() {
        return t_db;
    }

    public int getTracker_id() {
        return tracker_id;
    }

    public String getUpdated_on() {
        return updated_on;
    }

    public Integer getUser_story_id() {
        return user_story_id;
    }

    public void setAssigned_to_id(Integer assigned_to_id) {
        this.assigned_to_id = assigned_to_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDone_ratio(int done_ratio) {
        this.done_ratio = done_ratio;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public void setEstimated_hours(Float estimated_hours) {
        this.estimated_hours = estimated_hours;
    }

    public void setFixed_version_id(Integer fixed_version_id) {
        this.fixed_version_id = fixed_version_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLft(Integer lft) {
        this.lft = lft;
    }

    public void setLock_version(int lock_version) {
        this.lock_version = lock_version;
    }

    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public void setPriority_id(int priority_id) {
        this.priority_id = priority_id;
    }

    public void setProject_id(int project_id) {
        this.project_id = project_id;
    }

    public void setRedirect_to(String redirect_to) {
        this.redirect_to = redirect_to;
    }

    public void setRgt(Integer rgt) {
        this.rgt = rgt;
    }

    public void setRoot_id(Integer root_id) {
        this.root_id = root_id;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }

    public void setTracker_id(int tracker_id) {
        this.tracker_id = tracker_id;
    }

    public void setUpdated_on(String updated_on) {
        this.updated_on = updated_on;
    }

    public void setUser_story_id(Integer user_story_id) {
        this.user_story_id = user_story_id;
    }
}
