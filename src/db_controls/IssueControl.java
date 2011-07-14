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
import table_beans.Issue;
import table_beans.IssueCategory;
import table_beans.IssueRelation;
import table_beans.Journal;
import table_beans.JournalDetail;
import table_beans.Milestone;
import table_beans.TimeEstimate;
import table_beans.UserStory;
import table_beans.Version;
import table_beans.Watcher;

/**
 *
 * @author root
 */
public class IssueControl {

    private IdChanges idch;
    private SqlMapClient client = null;
    private String s_db;
    private String t_db;
    private static Logger logger = Logger.getLogger(IssueControl.class);

    public IssueControl(String s_db, String t_db) {
        try {
            idch = IdChanges.getInstance();
            client = AppSqlConfig.getInstance().getSqlMapper();
            this.s_db = s_db;
            this.t_db = t_db;
        } catch (Exception e) {
            logger.error("in constructor", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     *
     * @return versionID
     */
    private ArrayList<ArrayList> moveVersions() {

        ArrayList<ArrayList> report = new ArrayList<ArrayList>(0);
        report.add(new ArrayList(0));

        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("Versions");
        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("OLD_ID");
        report.get(report.size() - 1).add("OLD_NAME");
        report.get(report.size() - 1).add("STATUS");
        report.get(report.size() - 1).add("NEW_ID");
        //      report.get(report.size() - 1).add("NEW_NAME");

        List vers;
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        try {
            int old_prj = (Integer) idch.getProjectID().get(0).get(0);
            int new_prj = (Integer) idch.getProjectID().get(0).get(1);
            vers = client.queryForList("GetVersionsForProject", new ParamHelper(s_db, old_prj));

            for (int i = 0; i < vers.size(); ++i) {
                Version v = (Version) vers.get(i);

                report.add(new ArrayList(0));
                report.get(report.size() - 1).add(v.getId());
                report.get(report.size() - 1).add(v.getName());
                report.get(report.size() - 1).add("MOVED");



                v.setProject_id(new_prj);
                v.setT_db(t_db);
                client.insert("insertVersion", v);
                int newID = (Integer) client.queryForObject("LastInsertId", new ParamHelper(t_db, "versions"));
                res.add(new ArrayList(0));
                res.get(res.size() - 1).add(v.getId());
                res.get(res.size() - 1).add(newID);

                report.get(report.size() - 1).add(newID);

            }
            idch.addToReportPrj(report);
        } catch (Exception e) {
            logger.error("in moveVersions", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
        return res;
    }

    /**
     *
     * @return categoryID
     */
    private ArrayList<ArrayList> moveCategories() {
        ArrayList<ArrayList> report = new ArrayList<ArrayList>(0);
        report.add(new ArrayList(0));

        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("IssueCategories");
        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("OLD_ID");
        report.get(report.size() - 1).add("OLD_NAME");
        report.get(report.size() - 1).add("STATUS");
        report.get(report.size() - 1).add("NEW_ID");
        //     report.get(report.size() - 1).add("NEW_NAME");


        List categ;
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        try {
            int old_prj = (Integer) idch.getProjectID().get(0).get(0);
            int new_prj = (Integer) idch.getProjectID().get(0).get(1);
            categ = client.queryForList("GetCategoriesForProject", new ParamHelper(s_db, old_prj));

            for (int i = 0; i < categ.size(); ++i) {
                IssueCategory v = (IssueCategory) categ.get(i);

                report.add(new ArrayList(0));
                report.get(report.size() - 1).add(v.getId());
                report.get(report.size() - 1).add(v.getName());
                report.get(report.size() - 1).add("MOVED");


                v.setProject_id(new_prj);
                v.setT_db(t_db);
                client.insert("insertCategory", v);
                int newID = (Integer) client.queryForObject("LastInsertId", new ParamHelper(t_db, "issue_categories"));
                res.add(new ArrayList(0));
                res.get(res.size() - 1).add(v.getId());
                res.get(res.size() - 1).add(newID);
                report.get(report.size() - 1).add(newID);

            }
            idch.addToReportPrj(report);
        } catch (Exception e) {
            logger.error("in moveCategories", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //   e.printStackTrace();
        }
        return res;
    }

    /**
     * 
     * @return milestoneID
     */
    private ArrayList<ArrayList> moveMilestone() {
        List mlst;
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        try {
            int old_prj = (Integer) idch.getProjectID().get(0).get(0);
            int new_prj = (Integer) idch.getProjectID().get(0).get(1);
            mlst = client.queryForList("GetMilestoneForProject", new ParamHelper(s_db, old_prj));

            for (int i = 0; i < mlst.size(); ++i) {
                Milestone m = (Milestone) mlst.get(i);
                m.setProject_id(new_prj);
                m.setT_db(t_db);
                client.insert("insertMilestone", m);
                int newID = (Integer) client.queryForObject("LastInsertId", new ParamHelper(t_db, "milestones"));
                res.add(new ArrayList(0));
                res.get(res.size() - 1).add(m.getId());
                res.get(res.size() - 1).add(newID);
            }
        } catch (Exception e) {
            logger.error("in moveMilestone", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            ///    e.printStackTrace();
        }
        return res;
    }

    /**
     * 
     * @return timeEstimatesID
     */
    private ArrayList<ArrayList> moveTimeEstimates() {
        List te;
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        try {
            int old_prj = (Integer) idch.getProjectID().get(0).get(0);
            te = client.queryForList("GetTimeEstimateForProject", new ParamHelper(s_db, old_prj));

            for (int i = 0; i < te.size(); ++i) {
                TimeEstimate m = (TimeEstimate) te.get(i);
                m.setT_db(t_db);
                client.insert("insertTimeEstimate", m);
                int newID = (Integer) client.queryForObject("LastInsertId", new ParamHelper(t_db, "time_estimates"));
                res.add(new ArrayList(0));
                res.get(res.size() - 1).add(m.getId());
                res.get(res.size() - 1).add(newID);
            }
        } catch (Exception e) {
            logger.error("in moveTimeEstimates", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //  e.printStackTrace();
        }
        return res;
    }

    /**
     *
     * @return  UserStoriesID
     */
    private ArrayList<ArrayList> moveUserStories() {

        ArrayList<ArrayList> report = new ArrayList<ArrayList>(0);
        report.add(new ArrayList(0));

        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("UserStories");
        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("OLD_ID");
        report.get(report.size() - 1).add("OLD_NAME");
        report.get(report.size() - 1).add("STATUS");
        report.get(report.size() - 1).add("NEW_ID");
        //    report.get(report.size() - 1).add("NEW_NAME");


        List us;
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        try {
            int old_prj = (Integer) idch.getProjectID().get(0).get(0);
            int new_prj = (Integer) idch.getProjectID().get(0).get(1);
            us = client.queryForList("GetUserStoriesForProject", new ParamHelper(s_db, old_prj));

            for (int i = 0; i < us.size(); ++i) {
                UserStory m = (UserStory) us.get(i);

                report.add(new ArrayList(0));
                report.get(report.size() - 1).add(m.getId());
                report.get(report.size() - 1).add(m.getName());
                report.get(report.size() - 1).add("MOVED");


                Object milestoneOld = m.getMilestone_id();
                if (milestoneOld != null) {
                    m.setMilestone_id(idch.evaluateID(idch.getMilestoneID(), (Integer) m.getMilestone_id()));
                }
                Object estimatedOld = m.getTime_estimate_id();
                if (estimatedOld != null) {
                    m.setTime_estimate_id(idch.evaluateID(idch.getTimeEstimatesID(), (Integer) m.getTime_estimate_id()));
                }
                Object versionOld = m.getVersion_id();
                if (versionOld != null) {
                    m.setVersion_id(idch.evaluateID(idch.getVersionID(), (Integer) m.getVersion_id()));
                }

                m.setProject_id(new_prj);
                m.setT_db(t_db);
                client.insert("insertUserStory", m);
                int newID = (Integer) client.queryForObject("LastInsertId", new ParamHelper(t_db, "user_stories"));
                res.add(new ArrayList(0));
                res.get(res.size() - 1).add(m.getId());
                res.get(res.size() - 1).add(newID);
                report.get(report.size() - 1).add(newID);

            }
            idch.addToReportPrj(report);
        } catch (Exception e) {
            logger.error("in moveUserStories", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //           e.printStackTrace();
        }
        return res;
    }

    /**
     *
     * @return  issueID
     */
    private ArrayList<ArrayList> moveIssues() {
        ArrayList<ArrayList> report = new ArrayList<ArrayList>(0);
        report.add(new ArrayList(0));

        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("Issues");
        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("OLD_ID");
        report.get(report.size() - 1).add("OLD_SUBJECT");
        report.get(report.size() - 1).add("STATUS");
        report.get(report.size() - 1).add("NEW_ID");
        //report.get(report.size() - 1).add("NEW_SUBJECT");


        List issues;
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        try {
            int old_prj = (Integer) idch.getProjectID().get(0).get(0);
            int new_prj = (Integer) idch.getProjectID().get(0).get(1);
            issues = client.queryForList("GetIssuesForProject", new ParamHelper(s_db, old_prj));

            for (int i = 0; i < issues.size(); ++i) {
                Issue m = (Issue) issues.get(i);

                report.add(new ArrayList(0));
                report.get(report.size() - 1).add(m.getId());
                report.get(report.size() - 1).add(m.getSubject());
                report.get(report.size() - 1).add("MOVED");


                m.setTracker_id(idch.evaluateID(idch.getTrackerID(), m.getTracker_id()));
                m.setProject_id(new_prj);
                if (m.getCategory_id() != null) {
                    m.setCategory_id(idch.evaluateID(idch.getCategoryID(), (Integer) m.getCategory_id()));
                }
                m.setStatus_id(idch.evaluateID(idch.getStatusID(), m.getStatus_id()));
                if (m.getAssigned_to_id() != null) {
                    m.setAssigned_to_id(idch.evaluateID(idch.getUserID(), (Integer) m.getAssigned_to_id()));
                }
                m.setPriority_id(idch.evaluateID(idch.getEnumID(), m.getPriority_id()));
                if (m.getFixed_version_id() != null) {
                    m.setFixed_version_id(idch.evaluateID(idch.getVersionID(), (Integer) m.getFixed_version_id()));
                }
                m.setAuthor_id(idch.evaluateID(idch.getUserID(), m.getAuthor_id()));
                if (m.getUser_story_id() != null) {
                    m.setUser_story_id(idch.evaluateID(idch.getUserStoryID(), (Integer) m.getUser_story_id()));
                }
                if (m.getParent_id() != null) {
                    m.setParent_id(idch.evaluateID(res, (Integer) m.getParent_id()));
                }
                if (m.getRoot_id() != null) {
                    m.setRoot_id(idch.evaluateID(idch.getIssueID(), (Integer) m.getRoot_id()));
                }

                m.setT_db(t_db);
                client.insert("insertIssue", m);
                int newID = (Integer) client.queryForObject("LastInsertId", new ParamHelper(t_db, "issues"));
                if ((m.getRoot_id() != null) && (m.getRoot_id() == -1)) {
                    client.update("updateIssue", new ParamHelper(t_db, newID));
                }
                res.add(new ArrayList(0));
                res.get(res.size() - 1).add(m.getId());
                res.get(res.size() - 1).add(newID);
                idch.setIssueID(res);
                idch.addToJounals(moveJournals(m.getId()));   //moving part of journals
                idch.addToWatcherID(moveWatchers(m.getId()));
                report.get(report.size() - 1).add(newID);

            }
            idch.addToReportPrj(report);
        } catch (Exception e) {
            logger.error("in moveIssues", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //       e.printStackTrace();
        }
        return res;
    }

    /**
     *
     * @param id - issue id that is parent for all moving journals
     * @return part of journalID
     */
    private ArrayList<ArrayList> moveJournals(int id) {
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        try {
            List jrnl = client.queryForList("GetJournalsForEntity", new ParamHelper(s_db, id));
            for (int i = 0; i < jrnl.size(); ++i) {
                Journal cur = (Journal) jrnl.get(i);
                cur.setT_db(t_db);
                cur.setJournalized_id(idch.evaluateID(idch.getIssueID(), id));
                cur.setUser_id(idch.evaluateID(idch.getUserID(), cur.getUser_id()));
                client.insert("insertJournal", cur);
                res.add(new ArrayList(0));
                res.get(res.size() - 1).add(cur.getId());
                int last_id = (Integer) client.queryForObject("LastInsertId", new ParamHelper(t_db, "journals"));
                res.get(res.size() - 1).add(last_id);
            }
        } catch (Exception e) {
            logger.error("in moveJournals", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //  e.printStackTrace();
        }
        return res;
    }

    /**
     *
     * move all journals details for all journals in idch.getJournalID
     * @return part of journalDetailID
     */
    private ArrayList<ArrayList> moveJournalDetails() {
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        try {
            ArrayList<ArrayList> this_jrnl = idch.getJournalsID();
            for (int k = 0; k < this_jrnl.size(); ++k) {
                int old_jrnl_id = Integer.parseInt(this_jrnl.get(k).get(0).toString());
                int new_jrnl_id = Integer.parseInt(this_jrnl.get(k).get(1).toString());

                List jrnl = client.queryForList("GetJournalDetailsForEntity", new ParamHelper(s_db, old_jrnl_id));
                for (int i = 0; i < jrnl.size(); ++i) {
                    JournalDetail jd = (JournalDetail) jrnl.get(i);
                    jd.setT_db(t_db);
                    jd.setJournal_id(new_jrnl_id);
                    String s = jd.getProp_key();
                    //String tmp = "";

                    if ((jd.getOld_value() != null)) {

                        if (s.compareTo("user_story_id") == 0) {
                            Integer tmp = idch.evaluateID(idch.getUserStoryID(), Integer.parseInt(jd.getOld_value().toString()));
                            jd.setOld_value(((tmp == -1) ? (null) : (tmp)) + "");
                        } else {
                            if (s.compareTo("tracker_id") == 0) {
                                Integer tmp = idch.evaluateID(idch.getTrackerID(), Integer.parseInt(jd.getOld_value().toString()));
                                jd.setOld_value(((tmp == -1) ? (null) : (tmp)) + "");
                            } else {
                                if (s.compareTo("status_id") == 0) {
                                    Integer tmp = idch.evaluateID(idch.getStatusID(), Integer.parseInt(jd.getOld_value().toString()));
                                    jd.setOld_value(((tmp == -1) ? (null) : (tmp)) + "");
                                } else {
                                    if (s.compareTo("priority_id") == 0) {
                                        Integer tmp = idch.evaluateID(idch.getEnumID(), Integer.parseInt(jd.getOld_value().toString()));
                                        jd.setOld_value(((tmp == -1) ? (null) : (tmp)) + "");
                                    } else {
                                        if (s.compareTo("fixed_version_id") == 0) {
                                            Integer tmp = idch.evaluateID(idch.getVersionID(), Integer.parseInt(jd.getOld_value().toString()));
                                            jd.setOld_value(((tmp == -1) ? (null) : (tmp)) + "");
                                        } else {
                                            if (s.compareTo("category_id") == 0) {
                                                Integer tmp = idch.evaluateID(idch.getCategoryID(), Integer.parseInt(jd.getOld_value().toString()));
                                                jd.setOld_value(((tmp == -1) ? (null) : (tmp)) + "");
                                            } else {
                                                if (s.compareTo("assigned_to_id") == 0) {
                                                    Integer tmp = idch.evaluateID(idch.getUserID(), Integer.parseInt(jd.getOld_value().toString()));
                                                    jd.setOld_value(((tmp == -1) ? (null) : (tmp)) + "");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if ((jd.getValue() != null)) {

                        if (s.compareTo("user_story_id") == 0) {
                            Integer tmp = idch.evaluateID(idch.getUserStoryID(), Integer.parseInt(jd.getValue().toString()));
                            jd.setValue(((tmp == -1) ? (null) : (tmp)) + "");
                        } else {
                            if (s.compareTo("tracker_id") == 0) {
                                Integer tmp = idch.evaluateID(idch.getTrackerID(), Integer.parseInt(jd.getValue().toString()));
                                jd.setValue(((tmp == -1) ? (null) : (tmp)) + "");
                            } else {
                                if (s.compareTo("status_id") == 0) {
                                    Integer tmp = idch.evaluateID(idch.getStatusID(), Integer.parseInt(jd.getValue().toString()));
                                    jd.setValue(((tmp == -1) ? (null) : (tmp)) + "");
                                } else {
                                    if (s.compareTo("priority_id") == 0) {
                                        Integer tmp = idch.evaluateID(idch.getEnumID(), Integer.parseInt(jd.getValue().toString()));
                                        jd.setValue(((tmp == -1) ? (null) : (tmp)) + "");
                                    } else {
                                        if (s.compareTo("fixed_version_id") == 0) {
                                            Integer tmp = idch.evaluateID(idch.getVersionID(), Integer.parseInt(jd.getValue().toString()));
                                            jd.setValue(((tmp == -1) ? (null) : (tmp)) + "");
                                        } else {
                                            if (s.compareTo("category_id") == 0) {
                                                Integer tmp = idch.evaluateID(idch.getCategoryID(), Integer.parseInt(jd.getValue().toString()));
                                                jd.setValue(((tmp == -1) ? (null) : (tmp)) + "");
                                            } else {
                                                if (s.compareTo("assigned_to_id") == 0) {
                                                    Integer tmp = idch.evaluateID(idch.getUserID(), Integer.parseInt(jd.getValue().toString()));
                                                    jd.setValue(((tmp == -1) ? (null) : (tmp)) + "");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    client.insert("insertJournalDetail", jd);
                    res.add(new ArrayList<ArrayList>(0));
                    res.get(res.size() - 1).add(jd.getId());
                    Integer last_id = (Integer) client.queryForObject("LastInsertId", new ParamHelper(t_db, "journal_details"));
                    res.get(res.size() - 1).add(last_id);
                }
            }
        } catch (Exception e) {
            logger.error("in moveJournalDetails", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //        e.printStackTrace();
        }
        return res;
    }

    public ArrayList<ArrayList> moveWatchers(int id) {
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        try {
            List jrnl = client.queryForList("GetWatcherForEntity", new ParamHelper(s_db, id, "issue"));
            for (int i = 0; i < jrnl.size(); ++i) {
                Watcher cur = (Watcher) jrnl.get(i);
                cur.setT_db(t_db);
                cur.setWatchable_id(idch.evaluateID(idch.getIssueID(), cur.getWatchable_id()));
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
            //          e.printStackTrace();
        }
        return res;
    }

    /**
     *
     * @return  IssueRelID
     */
    public ArrayList<ArrayList> moveIssueRelations() {
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        try {
            List lst = client.queryForList("GetIssueRelations", new ParamHelper(s_db, ""));
            for (int i = 0; i < lst.size(); ++i) {
                IssueRelation cur = (IssueRelation) lst.get(i);
                Integer old_rel = idch.evaluateID(idch.getIssueID(), cur.getIssue_from_id());
                Integer new_rel = idch.evaluateID(idch.getIssueID(), cur.getIssue_to_id());
                if ((old_rel == -1) || (new_rel == -1)) {
                    continue;
                }
                cur.setT_db(t_db);
                cur.setIssue_from_id(old_rel);
                cur.setIssue_to_id(new_rel);

                client.insert("insertIssueRelation", cur);
                res.add(new ArrayList(0));
                res.get(res.size() - 1).add(cur.getId());
                int last_id = (Integer) client.queryForObject("LastInsertId", new ParamHelper(t_db, "issue_relations"));
                res.get(res.size() - 1).add(last_id);
            }
        } catch (Exception e) {
            logger.error("in moveIssueRelations", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //       e.printStackTrace();
        }
        return res;
    }

    /**
     *
     * @return true if all OK
     */
    public boolean moveIssuesWithRelatedAttributes() {
        try {
            idch.setVersionID(this.moveVersions());
            //         JOptionPane.showMessageDialog(null, "idch.setVersionID(this.moveVersions());", "Info", JOptionPane.INFORMATION_MESSAGE);
            idch.setCategoryID(this.moveCategories());
//            for (int i = 0; i < idch.getCategoryID().size(); ++i) {
//                System.out.println(idch.getCategoryID().get(i).get(0) + " --- " + idch.getCategoryID().get(i).get(1));
//            }
            //           JOptionPane.showMessageDialog(null, "idch.setCategoryID(this.moveCategories());", "Info", JOptionPane.INFORMATION_MESSAGE);
            idch.setMilestoneID(this.moveMilestone());
            //          JOptionPane.showMessageDialog(null, "idch.setMilestoneID(this.moveMilestone());", "Info", JOptionPane.INFORMATION_MESSAGE);
            idch.setTimeEstimatesID(this.moveTimeEstimates());
            //         JOptionPane.showMessageDialog(null, "idch.setTimeEstimatesID(this.moveTimeEstimates());", "Info", JOptionPane.INFORMATION_MESSAGE);
            idch.setUserStoryID(this.moveUserStories());
            //        JOptionPane.showMessageDialog(null, "idch.setUserStoryID(this.moveUserStories());", "Info", JOptionPane.INFORMATION_MESSAGE);
            idch.setIssueID(this.moveIssues());
            //       JOptionPane.showMessageDialog(null, "idch.setIssueID(this.moveIssues());", "Info", JOptionPane.INFORMATION_MESSAGE);
            idch.setJournalDetailsID(this.moveJournalDetails());

            moveIssueRelations();
            //   JOptionPane.showMessageDialog(null, "idch.setJournalDetailsID(this.moveJournalDetails());", "Info", JOptionPane.INFORMATION_MESSAGE);
            logger.info("moveIssuesWithRelatedAttributes     completed");
        } catch (Exception e) {
            logger.error("in moveIssuesWithRelatedAttributes", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
    ///        e.printStackTrace();
        }
        return true;
    }
}
