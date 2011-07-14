/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db_controls;

import com.ibatis.sqlmap.client.SqlMapClient;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import project_copy.IdChanges;
import table_beans.Change;
import table_beans.Changeset;
import table_beans.ChangesetsIssue;
import table_beans.Repository;

/**
 *
 * @author root
 */
public class RepositoryControl {

    private IdChanges idch;
    private SqlMapClient client = null;
    private String s_db;
    private String t_db;
    private static Logger logger = Logger.getLogger(RepositoryControl.class);

    public RepositoryControl(String s_db, String t_db) {
        try {
            idch = IdChanges.getInstance();
            client = AppSqlConfig.getInstance().getSqlMapper();
            this.s_db = s_db;
            this.t_db = t_db;
        } catch (Exception e) {
            logger.error("in constructor", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
    }

    public ArrayList<ArrayList> moveRepositories() {
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        int old_prj = (Integer) idch.getProjectID().get(0).get(0);
        int new_prj = (Integer) idch.getProjectID().get(0).get(1);
        try {
            List lst = client.queryForList("GetRepositoriesForIssue", new ParamHelper(s_db, old_prj));
            for (int i = 0; i < lst.size(); ++i) {
                Repository r = (Repository) lst.get(i);
                r.setProject_id(new_prj);
                r.setT_db(t_db);
                client.insert("insertRepository", r);

                res.add(new ArrayList(0));
                int newID = (Integer) client.queryForObject("LastInsertId", new ParamHelper(t_db, "repositories"));
                res.get(res.size() - 1).add(r.getId());
                res.get(res.size() - 1).add(newID);
            }
        } catch (Exception e) {
            logger.error("in moveRepositories", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
        return res;
    }

    public ArrayList<ArrayList> moveChangesets() {
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        try {
            for (int i = 0; i < idch.getRepositoryID().size(); ++i) {
                Integer old_rep = (Integer) idch.getRepositoryID().get(i).get(0);
                Integer new_rep = (Integer) idch.getRepositoryID().get(i).get(1);
                List lst = client.queryForList("GetChangesets", new ParamHelper(s_db, old_rep));
                for (int j = 0; j < lst.size(); ++j) {
                    Changeset c = (Changeset) lst.get(j);
                    c.setT_db(t_db);
                    c.setRepository_id(new_rep);
                    if (c.getUser_id() != null) {
                        c.setUser_id(idch.evaluateID(idch.getUserID(), c.getUser_id()));
                    }

                    client.insert("insertChangeset", c);
                    res.add(new ArrayList(0));
                    int newID = (Integer) client.queryForObject("LastInsertId", new ParamHelper(t_db, "changesets"));
                    res.get(res.size() - 1).add(c.getId());
                    res.get(res.size() - 1).add(newID);
                }
            }
        } catch (Exception e) {
            logger.error("in moveChangesets", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
        return res;

    }

    public ArrayList<ArrayList> moveChanges() {
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        try {
            for (int i = 0; i < idch.getChangesetID().size(); ++i) {
                Integer old_ch = (Integer) idch.getChangesetID().get(i).get(0);
                Integer new_ch = (Integer) idch.getChangesetID().get(i).get(1);
                List lst = client.queryForList("GetChanges", new ParamHelper(s_db, old_ch));
                for (int j = 0; j < lst.size(); ++j) {
                    Change c = (Change) lst.get(j);
                    c.setT_db(t_db);
                    c.setChangeset_id(new_ch);

                    client.insert("insertChange", c);
                    res.add(new ArrayList(0));
                    int newID = (Integer) client.queryForObject("LastInsertId", new ParamHelper(t_db, "changes"));
                    res.get(res.size() - 1).add(c.getId());
                    res.get(res.size() - 1).add(newID);
                }
            }
        } catch (Exception e) {
            logger.error("in moveChanges", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
        return res;
    }

    public void moveChangesetsIssues() {
        try {
            for (int i = 0; i < idch.getChangesetID().size(); ++i) {
                Integer old_ch = (Integer) idch.getChangesetID().get(i).get(0);
                Integer new_ch = (Integer) idch.getChangesetID().get(i).get(1);
                List lst = client.queryForList("GetChangesetsIssues", new ParamHelper(s_db, old_ch));
                for (int j = 0; j < lst.size(); ++j) {
                    ChangesetsIssue c = (ChangesetsIssue) lst.get(j);
                    c.setT_db(t_db);
                    c.setChangeset_id(new_ch);
                    c.setIssue_id(idch.evaluateID(idch.getIssueID(), c.getIssue_id()));
                    client.insert("insertChangesetsIssue", c);
                }
            }
        } catch (Exception e) {
            logger.error("in moveChangesetsIssues", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
    }
}
