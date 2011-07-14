/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db_controls;

import com.ibatis.sqlmap.client.SqlMapClient;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import project_copy.IdChanges;
import table_beans.Attachment;
import table_beans.Document;
import table_beans.EnabledModule;
import table_beans.ProjectTracker;
import table_beans.Query;
import table_beans.Question;
import table_beans.TimeEntry;
import table_beans.Token;
import table_beans.UserPref;

/**
 *
 * @author root
 */
public class AddonsControl {

    private IdChanges idch;
    private SqlMapClient client = null;
    private String s_db;
    private String t_db;
    private static Logger logger = Logger.getLogger(AddonsControl.class);

    public AddonsControl(String s_db, String t_db) {
        try {
            idch = IdChanges.getInstance();
            client = AppSqlConfig.getInstance().getSqlMapper();
            this.s_db = s_db;
            this.t_db = t_db;
            logger.info("Successful created");
        } catch (Exception e) {
            logger.error("in constructor", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //   e.printStackTrace();
        }
    }

    public void moveEnabledModules() {

        ArrayList<ArrayList> report = new ArrayList<ArrayList>(0);
        report.add(new ArrayList(0));

        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("Enabled_modules");
        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("OLD_ID");
        report.get(report.size() - 1).add("NAME");
        report.get(report.size() - 1).add("STATUS");


        int old_prj = (Integer) idch.getProjectID().get(0).get(0);
        int new_prj = (Integer) idch.getProjectID().get(0).get(1);
        try {
            List lst = client.queryForList("GetEnabledModulesForProject", new ParamHelper(s_db, old_prj));
            for (int i = 0; i < lst.size(); ++i) {
                EnabledModule m = (EnabledModule) lst.get(i);

                report.add(new ArrayList(0));
                report.get(report.size() - 1).add(m.getId());
                report.get(report.size() - 1).add(m.getName());
                report.get(report.size() - 1).add("MOVED");


                m.setProject_id(new_prj);
                m.setT_db(t_db);
                client.insert("insertEnabledModule", m);
            }
            idch.addToReportPrj(report);
            //     JOptionPane.showMessageDialog(null, "client.insert(\"insertEnabledModule\", m);", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            logger.error(" in moveEnabledModules:  " + e.getStackTrace(), e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //    e.printStackTrace();
        }
    }

    public void moveProjectTrackers() {
        try {
            int old_prj = (Integer) idch.getProjectID().get(0).get(0);
            int new_prj = (Integer) idch.getProjectID().get(0).get(1);

            List lst = client.queryForList("GetProjectTrackersForProject", new ParamHelper(s_db, old_prj));

            for (int i = 0; i < lst.size(); ++i) {
                ProjectTracker p = (ProjectTracker) lst.get(i);
                p.setT_db(t_db);
                p.setProject_id(new_prj);
                p.setTracker_id(idch.evaluateID(idch.getTrackerID(), p.getTracker_id()));
                client.insert("insertProjectTracker", p);
            }

        } catch (Exception e) {
            logger.error(" in moveProjectTrackers:  " + e.getStackTrace(), e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //    e.printStackTrace();
        }
    }

    /**
     *
     * @return  documentsID
     */
    public ArrayList<ArrayList> moveDocuments() {
        ArrayList<ArrayList> report = new ArrayList<ArrayList>(0);
        report.add(new ArrayList(0));

        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("Documents");
        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("OLD_ID");
        report.get(report.size() - 1).add("TITLE");
        report.get(report.size() - 1).add("STATUS");
        report.get(report.size() - 1).add("NEW_ID");

        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        try {
            int old_prj = (Integer) idch.getProjectID().get(0).get(0);
            int new_prj = (Integer) idch.getProjectID().get(0).get(1);

            List lst = client.queryForList("GetDocumentsForProject", new ParamHelper(s_db, old_prj));

            for (int i = 0; i < lst.size(); ++i) {

                Document p = (Document) lst.get(i);

                report.add(new ArrayList(0));
                report.get(report.size() - 1).add(p.getId());
                report.get(report.size() - 1).add(p.getTitle());
                report.get(report.size() - 1).add("MOVED");


                p.setT_db(t_db);
                p.setProject_id(new_prj);
                p.setCategory_id(idch.evaluateID(idch.getEnumID(), p.getCategory_id()));
                client.insert("insertDocument", p);
                res.add(new ArrayList(0));
                int newID = (Integer) client.queryForObject("LastInsertId", new ParamHelper(t_db, "documents"));
                res.get(res.size() - 1).add(p.getId());
                res.get(res.size() - 1).add(newID);
                report.get(report.size() - 1).add(newID);
            }
            idch.addToReportPrj(report);
        } catch (Exception e) {
            logger.error(" in moveDocuments:  " + e.getStackTrace(), e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //      e.printStackTrace();
        }
        return res;
    }

    /**
     * moving attachments;  writing disc_filenames into file
     * @return   attachmentID
     */
    public ArrayList<ArrayList> moveAttachments() {
        ArrayList<ArrayList> report = new ArrayList<ArrayList>(0);
        report.add(new ArrayList(0));

        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("Attachments");
        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("OLD_ID");
        report.get(report.size() - 1).add("FILENAME");
        report.get(report.size() - 1).add("DISK_FILENAME");
        report.get(report.size() - 1).add("STATUS");
        report.get(report.size() - 1).add("NEW_ID");

        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        ArrayList files = new ArrayList(0);
        try {
//issues
            for (int i = 0; i < idch.getIssueID().size(); ++i) {
                int old_issue = Integer.parseInt(idch.getIssueID().get(i).get(0).toString());
                int new_issue = Integer.parseInt(idch.getIssueID().get(i).get(1).toString());
                List lst = client.queryForList("GetAttachmentForProject", new ParamHelper(s_db, old_issue, "issue"));
                for (int j = 0; j < lst.size(); ++j) {
                    Attachment a = (Attachment) lst.get(j);

                    report.add(new ArrayList(0));
                    report.get(report.size() - 1).add(a.getId());
                    report.get(report.size() - 1).add(a.getFilename());
                    report.get(report.size() - 1).add(a.getDisk_filename());
                    report.get(report.size() - 1).add("MOVED");


                    a.setT_db(t_db);
                    a.setContainer_id(idch.evaluateID(idch.getIssueID(), old_issue));
                    a.setAuthor_id(idch.evaluateID(idch.getUserID(), a.getAuthor_id()));
                    client.insert("insertAttachment", a);
                    res.add(new ArrayList(0));
                    int newID = (Integer) client.queryForObject("LastInsertId", new ParamHelper(t_db, "attachments"));
                    res.get(res.size() - 1).add(a.getId());
                    res.get(res.size() - 1).add(newID);
                    files.add(a.getDisk_filename());

                    report.get(report.size() - 1).add(newID);
                }
            }
//documents
            for (int i = 0; i < idch.getDocumentID().size(); ++i) {
                int old_doc = Integer.parseInt(idch.getDocumentID().get(i).get(0).toString());
                int new_doc = Integer.parseInt(idch.getDocumentID().get(i).get(1).toString());
                List lst = client.queryForList("GetAttachmentForProject", new ParamHelper(s_db, old_doc, "document"));
                for (int j = 0; j < lst.size(); ++j) {
                    Attachment a = (Attachment) lst.get(j);

                    report.add(new ArrayList(0));
                    report.get(report.size() - 1).add(a.getId());
                    report.get(report.size() - 1).add(a.getFilename());
                    report.get(report.size() - 1).add(a.getDisk_filename());
                    report.get(report.size() - 1).add("MOVED");


                    a.setT_db(t_db);
                    a.setContainer_id(idch.evaluateID(idch.getDocumentID(), old_doc));
                    a.setAuthor_id(idch.evaluateID(idch.getUserID(), a.getAuthor_id()));
                    client.insert("insertAttachment", a);
                    res.add(new ArrayList(0));
                    int newID = (Integer) client.queryForObject("LastInsertId", new ParamHelper(t_db, "attachments"));
                    res.get(res.size() - 1).add(a.getId());
                    res.get(res.size() - 1).add(newID);
                    files.add(a.getDisk_filename());

                    report.get(report.size() - 1).add(newID);
                }
            }
//messages
            for (int i = 0; i < idch.getMessageID().size(); ++i) {
                int old_mes = Integer.parseInt(idch.getMessageID().get(i).get(0).toString());
                int new_mes = Integer.parseInt(idch.getMessageID().get(i).get(1).toString());
                List lst = client.queryForList("GetAttachmentForProject", new ParamHelper(s_db, old_mes, "message"));
                for (int j = 0; j < lst.size(); ++j) {
                    Attachment a = (Attachment) lst.get(j);

                    report.add(new ArrayList(0));
                    report.get(report.size() - 1).add(a.getId());
                    report.get(report.size() - 1).add(a.getFilename());
                    report.get(report.size() - 1).add(a.getDisk_filename());
                    report.get(report.size() - 1).add("MOVED");

                    a.setT_db(t_db);
                    a.setContainer_id(idch.evaluateID(idch.getMessageID(), old_mes));
                    a.setAuthor_id(idch.evaluateID(idch.getUserID(), a.getAuthor_id()));
                    client.insert("insertAttachment", a);
                    res.add(new ArrayList(0));
                    int newID = (Integer) client.queryForObject("LastInsertId", new ParamHelper(t_db, "attachments"));
                    res.get(res.size() - 1).add(a.getId());
                    res.get(res.size() - 1).add(newID);
                    files.add(a.getDisk_filename());

                    report.get(report.size() - 1).add(newID);
                }
            }

//out to file attachments.txt
            //DataOutputStream dos = new DataOutputStream((new FileOutputStream("Attachments__" + s_db + "__to__" + t_db + ".txt", true)));
            BufferedWriter out = new BufferedWriter(new FileWriter("Attachments__" + s_db + "__to__" + t_db + ".txt", true));
            for (int i = 0; i < files.size(); ++i) {
                out.write(files.get(i).toString() + "\n");
            }
            out.close();

            idch.addToReportPrj(report);
        } catch (Exception e) {
            logger.error(" in moveAttachments:  ", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
        return res;
    }

    /**
     *
     * @return  documentsID
     */
    public ArrayList<ArrayList> moveTimeEntries() {
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        try {
            int old_prj = (Integer) idch.getProjectID().get(0).get(0);
            int new_prj = (Integer) idch.getProjectID().get(0).get(1);

            List lst = client.queryForList("GetTimeEntriesForProject", new ParamHelper(s_db, old_prj));

            for (int i = 0; i < lst.size(); ++i) {

                TimeEntry p = (TimeEntry) lst.get(i);
                p.setT_db(t_db);
                p.setProject_id(new_prj);
                p.setUser_id(idch.evaluateID(idch.getUserID(), p.getUser_id()));
                p.setIssue_id(idch.evaluateID(idch.getIssueID(), p.getIssue_id()));
                p.setActivity_id(idch.evaluateID(idch.getEnumID(), p.getActivity_id()));
                client.insert("insertTimeEntry", p);
                res.add(new ArrayList(0));
                int newID = (Integer) client.queryForObject("LastInsertId", new ParamHelper(t_db, "time_entries"));
                res.get(res.size() - 1).add(p.getId());
                res.get(res.size() - 1).add(newID);
            }

        } catch (Exception e) {
            logger.error(" in moveTimeEntries:  ", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //      e.printStackTrace();
        }
        return res;
    }

    /**
     * 
    GetQueriesForProject
    insertQuery
     */
    public ArrayList<ArrayList> moveQueries() {
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        try {
            int old_prj = (Integer) idch.getProjectID().get(0).get(0);
            int new_prj = (Integer) idch.getProjectID().get(0).get(1);

            List lst = client.queryForList("GetQueriesForProject", new ParamHelper(s_db, old_prj));

            for (int i = 0; i < lst.size(); ++i) {
                Query q = (Query) lst.get(i);

                q.setT_db(t_db);
                q.setProject_id(new_prj);
                q.setUser_id(idch.evaluateID(idch.getUserID(), q.getUser_id()));
                client.insert("insertQuery", q);
                res.add(new ArrayList(0));
                int newID = (Integer) client.queryForObject("LastInsertId", new ParamHelper(t_db, "queries"));
                res.get(res.size() - 1).add(q.getId());
                res.get(res.size() - 1).add(newID);
            }

        } catch (Exception e) {
            logger.error(" in moveQueries:  ", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }

        return res;
    }

    public ArrayList<ArrayList> moveUserPreferences() {
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        try {
            for (int i = 0; i < idch.getUserID().size(); ++i) {
                Integer old_usr = Integer.parseInt(idch.getUserID().get(i).get(0).toString());
                Integer new_usr = Integer.parseInt(idch.getUserID().get(i).get(1).toString());
                List lst = client.queryForList("GetUserPrefForProject", new ParamHelper(s_db, old_usr));
                for (int j = 0; j < lst.size(); ++j) {
                    UserPref u = (UserPref) lst.get(j);
                    u.setT_db(t_db);
                    u.setUser_id(new_usr);
                    client.insert("insertUserPref", u);
                    res.add(new ArrayList(0));
                    int newID = (Integer) client.queryForObject("LastInsertId", new ParamHelper(t_db, "user_preferences"));
                    res.get(res.size() - 1).add(u.getId());
                    res.get(res.size() - 1).add(newID);
                }
            }
        } catch (Exception e) {
            logger.error(" in moveUserPreferences:  ", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();

        }
        return res;
    }

    public ArrayList<ArrayList> moveQuestions() {
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        try {
            for (int i = 0; i < idch.getIssueID().size(); ++i) {
                Integer old_issue = (Integer) idch.getIssueID().get(i).get(0);
                Integer new_issue = (Integer) idch.getIssueID().get(i).get(1);
                List lst = client.queryForList("GetQuestionForIssue", new ParamHelper(s_db, old_issue));
                for (int j = 0; j < lst.size(); ++j) {
                    Question q = (Question) lst.get(j);
                    q.setT_db(t_db);
                    q.setJournal_id(idch.evaluateID(idch.getJournalsID(), q.getJournal_id()));
                    q.setAuthor_id(idch.evaluateID(idch.getUserID(), q.getAuthor_id()));
                    q.setAssigned_to_id(idch.evaluateID(idch.getUserID(), q.getAssigned_to_id()));
                    q.setIssue_id(new_issue);
                    client.insert("insertQuestion", q);
                    res.add(new ArrayList(0));
                    int newID = (Integer) client.queryForObject("LastInsertId", new ParamHelper(t_db, "questions"));
                    res.get(res.size() - 1).add(q.getId());
                    res.get(res.size() - 1).add(newID);
                }
            }
        } catch (Exception e) {
            logger.error(" in moveQuestions:  ", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();

        }
        return res;
    }

    public void moveTokens() {
        try {
            for (int i = 0; i < idch.getUserID().size(); ++i) {
                Integer old_usr = Integer.parseInt(idch.getUserID().get(i).get(0).toString());
                Integer new_usr = Integer.parseInt(idch.getUserID().get(i).get(1).toString());
                List lst = client.queryForList("GetTokens", new ParamHelper(s_db, old_usr));
                for (int j = 0; j < lst.size(); ++j) {
                    Token u = (Token) lst.get(j);
                    u.setT_db(t_db);
                    u.setUser_id(new_usr);
                    client.insert("insertToken", u);
                }
            }
        } catch (Exception e) {
            logger.error(" in moveTokens:  ", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
          //  e.printStackTrace();
        }
    }
}
