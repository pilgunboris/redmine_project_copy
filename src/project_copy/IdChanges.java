/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package project_copy;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 *
 * @author Pilgun Boris
 */
/**
 *by pattern Singleton
 */
public class IdChanges {

    private static Logger logger = Logger.getLogger(IdChanges.class);
    private static IdChanges instance = null;
    //--data section
    //-------------------------------
    //cahges id of project (two values old && new)
    private ArrayList<ArrayList> projectID;
    //users mails relations before moving
    private ArrayList<ArrayList> usersRelations;
    //users id changes (tho values old && new)
    private ArrayList<ArrayList> userID;
    //members ids changes (tho values old && new)
    private ArrayList<ArrayList> memberID;
    //roles names relations before moving
    private ArrayList<ArrayList> rolesRelations;
    //roles id changes (tho values old && new)
    private ArrayList<ArrayList> roleID;
    //Trackers names relations before moving
    private ArrayList<ArrayList> trackersRelations;
    //trackers id changes (tho values old && new)
    private ArrayList<ArrayList> trackerID;
    //statuses names relations before moving
    private ArrayList<ArrayList> statusesRelations;
    //statuses id changes (tho values old && new)
    private ArrayList<ArrayList> statusID;
    //member_roles id changes (tho values old && new)
    private ArrayList<ArrayList> member_rolesID;
    //enumerations RedmineEnum relations
    private ArrayList<ArrayList> enumRelations;
    //enumerations RedmineEnum id changes
    private ArrayList<ArrayList> enumID;
    //versions id changes
    private ArrayList<ArrayList> versionID;
    //category id changes
    private ArrayList<ArrayList> categoryID;
    //Milestone id changes
    private ArrayList<ArrayList> milestoneID;
    //timeEstimatesID id changes
    private ArrayList<ArrayList> timeEstimatesID;
    //userStoryID id changes
    private ArrayList<ArrayList> userStoryID;
    //issues id changes
    private ArrayList<ArrayList> issueID;
    //journals id changes
    private ArrayList<ArrayList> journalsID;
    //journalDetails id changes
    private ArrayList<ArrayList> journalDetailsID;
    //boardID
    private ArrayList<ArrayList> boardID;
    //messageID
    private ArrayList<ArrayList> messageID;
    //watcherID
    private ArrayList<ArrayList> watcherID;
    //documentID
    private ArrayList<ArrayList> documentID;
    //attachmentID
    private ArrayList<ArrayList> attachmentID;
    //timeEntryID
    private ArrayList<ArrayList> timeEntryID;
    //queryID
    private ArrayList<ArrayList> queryID;
    //repositoryID
    private ArrayList<ArrayList> repositoryID;
    //changesetID
    private ArrayList<ArrayList> changesetID;
    ////
    ///
    private ArrayList<ArrayList> reportE;
    //reportPrj
    private ArrayList<ArrayList> reportPrj;
    //-------------------------------

    private IdChanges() {
        //cahges id of project
        projectID = new ArrayList<ArrayList>(0);
        projectID.add(new ArrayList(0));
        projectID.get(0).add(0);
        projectID.get(0).add(0);

        //users mails relations before moving
        usersRelations = new ArrayList<ArrayList>(0);

        //users id changes (tho values old && new)
        userID = new ArrayList<ArrayList>(0);

        //members ids changes (tho values old && new)
        memberID = new ArrayList<ArrayList>(0);

        //roles names relations before moving
        rolesRelations = new ArrayList<ArrayList>(0);

        //roles id changes (tho values old && new)
        roleID = new ArrayList<ArrayList>(0);

        //Trackers names relations before moving
        trackersRelations = new ArrayList<ArrayList>(0);

        //trackers id changes (tho values old && new)
        trackerID = new ArrayList<ArrayList>(0);

        //statuses names relations before moving
        statusesRelations = new ArrayList<ArrayList>(0);

        //statuses id changes (tho values old && new)
        statusID = new ArrayList<ArrayList>(0);

        //member_roles id changes (tho values old && new)
        member_rolesID = new ArrayList<ArrayList>(0);

        //enumerations RedmineEnum relations
        enumRelations = new ArrayList<ArrayList>(0);

        //enumerations RedmineEnum id changes
        enumID = new ArrayList<ArrayList>(0);

        //versions id changes
        versionID = new ArrayList<ArrayList>(0);

        //categoryID chenges
        categoryID = new ArrayList<ArrayList>(0);

        //Milestone id changes
        milestoneID = new ArrayList<ArrayList>(0);

        //timeEstimatesID changes
        timeEstimatesID = new ArrayList<ArrayList>(0);

        //userStoryID id changes
        userStoryID = new ArrayList<ArrayList>(0);

        //issueID id changes
        issueID = new ArrayList<ArrayList>(0);

        //journalsID changes
        journalsID = new ArrayList<ArrayList>(0);

        //journalDetailsID
        journalDetailsID = new ArrayList<ArrayList>(0);

        //boardID
        boardID = new ArrayList<ArrayList>(0);

        //messageID
        messageID = new ArrayList<ArrayList>(0);

        //watcherID
        watcherID = new ArrayList<ArrayList>(0);

        //documentID
        documentID = new ArrayList<ArrayList>(0);

        //attachmentID
        attachmentID = new ArrayList<ArrayList>(0);

        //timeEntryID
        timeEntryID = new ArrayList<ArrayList>(0);

        //queryID
        queryID = new ArrayList<ArrayList>(0);

        //repositoryID
        repositoryID = new ArrayList<ArrayList>(0);

        //changesetID
        changesetID = new ArrayList<ArrayList>(0);

        //reportE
        reportE = new ArrayList<ArrayList>(0);

        //reportPrj
        reportPrj = new ArrayList<ArrayList>(0);
    }

    public static IdChanges getInstance() throws Exception {
        if (instance == null) {
            instance = new IdChanges();
        }
        return instance;
    }

    /**
     *
     * @param ids - list || ArrayList for old\new ids
     * @param old  - old id of the object
     * @return  new id;
     */
    public Integer evaluateID(ArrayList<ArrayList> ids, Integer old) {
        try {
            if (old == null) {
                return null;
            }
            if ((ids != null)) {
                for (int i = 0; i < ids.size(); ++i) {
                    int e_old = Integer.parseInt(ids.get(i).get(0).toString());
                    if (e_old == old) {
                        return Integer.parseInt(ids.get(i).get(1).toString());
                    }
                }
            }
        } catch (Exception e) {

            logger.error("in evaluateID", e);
            //e.printStackTrace();
        }
        //throw new NullPointerException("IdChanges___Such key not included");
        logger.warn("evaluateID   cought value \'-1\' ");
        return -1;
    }

    /**
     * ArrayList relations returns ArrayList<ArrayList> where elem(0) == oldvalue  and elem(1) == new value from some enumeration
     */
    public ArrayList<ArrayList> UpdateRelations(ArrayList<ArrayList> relations, String oldValue, String newValue) {
        try {
            boolean flag = false;
            for (int i = 0; i < relations.size(); ++i) {
                if (relations.get(i).get(0).toString().compareTo(oldValue) == 0) {
                    relations.get(i).set(1, newValue);
                    flag = true;
                }
            }
            if (!flag) {
                relations.add(new ArrayList(0));
                relations.get(relations.size() - 1).add(oldValue);
                relations.get(relations.size() - 1).add(newValue);
            }
        } catch (Exception e) {
            logger.error("in UpdateRelations", e);
            //e.printStackTrace();
        }
        return relations;
    }

    public String getPreparedIssue(ArrayList<ArrayList> relat) {
        String res = "";
        for (int i = 0; i < relat.size(); ++i) {
            res += "--------------------------------------\n"
                    + relat.get(i).get(0).toString() + "\n"
                    + "сопоставлен с \n"
                    + relat.get(i).get(1).toString() + "\n";
        }
        return res;
    }

    public void WriteReport(String fileName, ArrayList<ArrayList> in) {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("A1");
        try {
            for (int i = 0; i < in.size(); ++i) {
                HSSFRow dataRow = sheet.createRow(i);
                for (int j = 0; j < in.get(i).size(); ++j) {
                    dataRow.createCell(j);
                    sheet.getRow(i).getCell(j).setCellValue(in.get(i).get(j).toString());
                }
            }

            File f = new File(fileName);
            FileOutputStream fos = new FileOutputStream(f);
            wb.write(fos);
            fos.close();
        } catch (Exception e) {
            logger.error("in WriteReport", e);
            //e.printStackTrace();
        }
    }

    public void addToJounals(ArrayList<ArrayList> in) {
        for (int i = 0; i < in.size(); ++i) {
            this.journalsID.add(in.get(i));
        }
    }

    public void addToWatcherID(ArrayList<ArrayList> in) {
        for (int i = 0; i < in.size(); ++i) {
            this.watcherID.add(in.get(i));
        }
    }

    public void addToReportE(ArrayList<ArrayList> in) {
        for (int i = 0; i < in.size(); ++i) {
            this.reportE.add(in.get(i));
        }
    }

    public void addToReportPrj(ArrayList<ArrayList> in) {
        for (int i = 0; i < in.size(); ++i) {
            this.reportPrj.add(in.get(i));
        }
    }

    public ArrayList<ArrayList> getProjectID() {
        return projectID;
    }

    public ArrayList<ArrayList> getUsersRelations() {
        return usersRelations;
    }

    public ArrayList<ArrayList> getUserID() {
        return userID;
    }

    public void setUserID(ArrayList<ArrayList> userID) {
        this.userID = userID;
    }

    public void setUsersRelations(ArrayList<ArrayList> relations) {
        this.usersRelations = relations;
    }

    public ArrayList<ArrayList> getMemberID() {
        return memberID;
    }

    public void setMemberID(ArrayList<ArrayList> memberID) {
        this.memberID = memberID;
    }

    public ArrayList<ArrayList> getRolesRelations() {
        return rolesRelations;
    }

    public void setRolesRelations(ArrayList<ArrayList> rolesRelations) {
        this.rolesRelations = rolesRelations;
    }

    public ArrayList<ArrayList> getRoleID() {
        return roleID;
    }

    public void setRoleID(ArrayList<ArrayList> roleID) {
        this.roleID = roleID;
    }

    public ArrayList<ArrayList> getTrackerID() {
        return trackerID;
    }

    public ArrayList<ArrayList> getTrackersRelations() {
        return trackersRelations;
    }

    public void setTrackerID(ArrayList<ArrayList> trackerID) {
        this.trackerID = trackerID;
    }

    public void setTrackersRelations(ArrayList<ArrayList> trackersRelations) {
        this.trackersRelations = trackersRelations;
    }

    public ArrayList<ArrayList> getStatusID() {
        return statusID;
    }

    public ArrayList<ArrayList> getStatusesRelations() {
        return statusesRelations;
    }

    public void setStatusID(ArrayList<ArrayList> statusID) {
        this.statusID = statusID;
    }

    public void setStatusesRelations(ArrayList<ArrayList> statusesRelations) {
        this.statusesRelations = statusesRelations;
    }

    public ArrayList<ArrayList> getMember_rolesID() {
        return member_rolesID;
    }

    public void setMember_rolesID(ArrayList<ArrayList> member_rolesID) {
        this.member_rolesID = member_rolesID;
    }

    public ArrayList<ArrayList> getEnumID() {
        return enumID;
    }

    public ArrayList<ArrayList> getEnumRelations() {
        return enumRelations;
    }

    public void setEnumID(ArrayList<ArrayList> enumID) {
        this.enumID = enumID;
    }

    public void setEnumRelations(ArrayList<ArrayList> enumRelations) {
        this.enumRelations = enumRelations;
    }

    public ArrayList<ArrayList> getVersionID() {
        return versionID;
    }

    public void setVersionID(ArrayList<ArrayList> versionID) {
        this.versionID = versionID;
    }

    public void setCategoryID(ArrayList<ArrayList> categoryID) {
        this.categoryID = categoryID;
    }

    public ArrayList<ArrayList> getCategoryID() {
        return categoryID;
    }

    public ArrayList<ArrayList> getMilestoneID() {
        return milestoneID;
    }

    public void setMilestoneID(ArrayList<ArrayList> milestoneID) {
        this.milestoneID = milestoneID;
    }

    public ArrayList<ArrayList> getTimeEstimatesID() {
        return timeEstimatesID;
    }

    public void setTimeEstimatesID(ArrayList<ArrayList> timeEstimatesID) {
        this.timeEstimatesID = timeEstimatesID;
    }

    public ArrayList<ArrayList> getIssueID() {
        return issueID;
    }

    public ArrayList<ArrayList> getUserStoryID() {
        return userStoryID;
    }

    public void setIssueID(ArrayList<ArrayList> issueID) {
        this.issueID = issueID;
    }

    public void setUserStoryID(ArrayList<ArrayList> userStoryID) {
        this.userStoryID = userStoryID;
    }

    public ArrayList<ArrayList> getJournalsID() {
        return journalsID;
    }

    public void setJournalsID(ArrayList<ArrayList> journalsID) {
        this.journalsID = journalsID;
    }

    public ArrayList<ArrayList> getJournalDetailsID() {
        return journalDetailsID;
    }

    public void setJournalDetailsID(ArrayList<ArrayList> journalDetailsID) {
        this.journalDetailsID = journalDetailsID;
    }

    public ArrayList<ArrayList> getBoardID() {
        return boardID;
    }

    public void setBoardID(ArrayList<ArrayList> boardID) {
        this.boardID = boardID;
    }

    public ArrayList<ArrayList> getMessageID() {
        return messageID;
    }

    public void setMessageID(ArrayList<ArrayList> messageID) {
        this.messageID = messageID;
    }

    public ArrayList<ArrayList> getWatcherID() {
        return watcherID;
    }

    public void setWatcherID(ArrayList<ArrayList> watcherID) {
        this.watcherID = watcherID;
    }

    public void setDocumentID(ArrayList<ArrayList> documentID) {
        this.documentID = documentID;
    }

    public ArrayList<ArrayList> getDocumentID() {
        return documentID;
    }

    public ArrayList<ArrayList> getAttachmentID() {
        return attachmentID;
    }

    public void setAttachmentID(ArrayList<ArrayList> attachmentID) {
        this.attachmentID = attachmentID;
    }

    public ArrayList<ArrayList> getTimeEntryID() {
        return timeEntryID;
    }

    public void setTimeEntryID(ArrayList<ArrayList> timeEntryID) {
        this.timeEntryID = timeEntryID;
    }

    public ArrayList<ArrayList> getQueryID() {
        return queryID;
    }

    public void setQueryID(ArrayList<ArrayList> queryID) {
        this.queryID = queryID;
    }

    public void setRepositoryID(ArrayList<ArrayList> repositoryID) {
        this.repositoryID = repositoryID;
    }

    public ArrayList<ArrayList> getRepositoryID() {
        return repositoryID;
    }

    public ArrayList<ArrayList> getChangesetID() {
        return changesetID;
    }

    public void setChangesetID(ArrayList<ArrayList> changesetID) {
        this.changesetID = changesetID;
    }

    public ArrayList<ArrayList> getReportE() {
        return reportE;
    }

    public void setReportE(ArrayList<ArrayList> reportE) {
        this.reportE = reportE;
    }

    public ArrayList<ArrayList> getReportPrj() {
        return reportPrj;
    }

    public void setReportPrj(ArrayList<ArrayList> reportPrj) {
        this.reportPrj = reportPrj;
    }
}
