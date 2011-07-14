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
import table_beans.Board;
import table_beans.Message;
import table_beans.Watcher;

/**
 *
 * @author root
 */
public class BoardControl {

    private IdChanges idch;
    private SqlMapClient client = null;
    private String s_db;
    private String t_db;
    private static Logger logger = Logger.getLogger(BoardControl.class);

    public BoardControl(String s_db, String t_db) {
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

    private ArrayList<ArrayList> moveBoards() {
        ArrayList<ArrayList> report = new ArrayList<ArrayList>(0);
        report.add(new ArrayList(0));

        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("Boards");
        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("OLD_ID");
        report.get(report.size() - 1).add("NAME");
        report.get(report.size() - 1).add("STATUS");
        report.get(report.size() - 1).add("NEW_ID");

        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        try {

            int old_prj = (Integer) idch.getProjectID().get(0).get(0);
            int new_prj = (Integer) idch.getProjectID().get(0).get(1);

            List lst = client.queryForList("GetBoardsForProject", new ParamHelper(s_db, old_prj));
            for (int i = 0; i < lst.size(); ++i) {
                Board bd = (Board) lst.get(i);

                report.add(new ArrayList(0));
                report.get(report.size() - 1).add(bd.getId());
                report.get(report.size() - 1).add(bd.getName());
                report.get(report.size() - 1).add("MOVED");

                bd.setProject_id(new_prj);
                bd.setT_db(t_db);
                client.insert("insertBoard", bd);
                int newID = (Integer) client.queryForObject("LastInsertId", new ParamHelper(t_db, "boards"));
                res.add(new ArrayList(0));
                res.get(res.size() - 1).add(bd.getId());
                res.get(res.size() - 1).add(newID);
                report.get(report.size() - 1).add(newID);
            }
            idch.addToReportPrj(report);
        } catch (Exception e) {
            logger.error("in constructor", e);
            logger.error("in moveBoards", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            // e.printStackTrace();
        }
        return res;
    }

    private ArrayList<ArrayList> moveMessages() {
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        try {
            ArrayList<ArrayList> brd = idch.getBoardID();
            for (int i = 0; i < brd.size(); ++i) {
                int old_brd = (Integer) brd.get(i).get(0);
                int new_brd = (Integer) brd.get(i).get(1);
                List lst = client.queryForList("GetMessagesForProject", new ParamHelper(s_db, old_brd));
                for (int j = 0; j < lst.size(); ++j) {
                    Message m = (Message) lst.get(j);
                    m.setT_db(t_db);
                    m.setBoard_id(new_brd);
                    m.setAuthor_id(idch.evaluateID(idch.getUserID(), m.getAuthor_id()));
                    client.insert("insertMessage", m);
                    int newID = (Integer) client.queryForObject("LastInsertId", new ParamHelper(t_db, "messages"));
                    res.add(new ArrayList(0));
                    res.get(res.size() - 1).add(m.getId());
                    res.get(res.size() - 1).add(newID);
                    idch.addToWatcherID(moveWatchers(res, m.getId()));
                }
//update messages
                lst = client.queryForList("GetMessagesForProject", new ParamHelper(t_db, new_brd));
                for (int j = 0; j < lst.size(); ++j) {
                    Message m = (Message) lst.get(j);
                    m.setT_db(t_db);

                    Integer parent = -1;
                    Integer last_reply = -1;

                    if (m.getParent_id() != null) {
                        parent = idch.evaluateID(res, m.getParent_id());
                    }
                    if (m.getLast_reply_id() != null) {
                        last_reply = idch.evaluateID(res, m.getLast_reply_id());
                    }

                    m.setParent_id((parent != -1) ? (parent) : (null));
                    m.setLast_reply_id((last_reply != -1) ? (last_reply) : (null));
                    client.update("updateMessage", new ParamHelper(t_db, m.getId(), m.getLast_reply_id(), m.getParent_id()));
                }
            }

        } catch (Exception e) {
            logger.error("in moveMessages", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //  e.printStackTrace();
        }
        return res;
    }

    public ArrayList<ArrayList> moveWatchers(ArrayList<ArrayList> in, int id) {
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        try {
            List jrnl = client.queryForList("GetWatcherForEntity", new ParamHelper(s_db, id, "message"));
            for (int i = 0; i < jrnl.size(); ++i) {
                Watcher cur = (Watcher) jrnl.get(i);
                cur.setT_db(t_db);
                cur.setWatchable_id(idch.evaluateID(in, cur.getWatchable_id()));
                cur.setUser_id(idch.evaluateID(idch.getUserID(), cur.getUser_id()));
                client.insert("insertWatcher", cur);
                res.add(new ArrayList(0));
                res.get(res.size() - 1).add(cur.getId());
                int last_id = (Integer) client.queryForObject("LastInsertId", new ParamHelper(t_db, "watchers"));
                res.get(res.size() - 1).add(last_id);
            }
        } catch (Exception e) {
            logger.error("in moveWatchers", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //  e.printStackTrace();
        }
        return res;
    }

    public boolean moveBoardsWithRelatedMessages() {
        try {
            idch.setBoardID(this.moveBoards());
            //     JOptionPane.showMessageDialog(null, "idch.setBoardID(this.moveBoards());", "Info", JOptionPane.INFORMATION_MESSAGE);
            idch.setMessageID(this.moveMessages());
            //     JOptionPane.showMessageDialog(null, "idch.setMessageID(this.moveMessages());", "Info", JOptionPane.INFORMATION_MESSAGE);

            logger.info("moveBoardsWithRelatedMessages     completed");
        } catch (Exception e) {
            logger.error("in moveBoardsWithRelatedMessages", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //    e.printStackTrace();
        }
        return true;
    }
}
