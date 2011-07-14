/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

/**
 *
 * @author root
 */
public class Attachment {

    private int id;
    private Integer container_id;
    private String container_type;
    private String filename;
    private String disk_filename;
    private Integer filesize;
    private String content_type;
    private String digest;
    private Integer downloads;
    private Integer author_id;
    private String created_on;
    private String description;
    /////
    private String t_db;

    public Attachment() {
    }

    public Integer getAuthor_id() {
        return author_id;
    }

    public Integer getContainer_id() {
        return container_id;
    }

    public String getContainer_type() {
        return container_type;
    }

    public String getContent_type() {
        return content_type;
    }

    public String getCreated_on() {
        return created_on;
    }

    public String getDescription() {
        return description;
    }

    public String getDigest() {
        return digest;
    }

    public String getDisk_filename() {
        return disk_filename;
    }

    public Integer getDownloads() {
        return downloads;
    }

    public String getFilename() {
        return filename;
    }

    public Integer getFilesize() {
        return filesize;
    }

    public int getId() {
        return id;
    }

    public void setAuthor_id(Integer author_id) {
        this.author_id = author_id;
    }

    public void setContainer_id(Integer container_id) {
        this.container_id = container_id;
    }

    public void setContainer_type(String container_type) {
        this.container_type = container_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public void setDisk_filename(String disk_filename) {
        this.disk_filename = disk_filename;
    }

    public void setDownloads(Integer downloads) {
        this.downloads = downloads;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setFilesize(Integer filesize) {
        this.filesize = filesize;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getT_db() {
        return t_db;
    }

    public void setT_db(String t_db) {
        this.t_db = t_db;
    }
}
