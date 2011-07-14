/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table_beans;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import project_copy.ConnectToDb;
import project_copy.IdChanges;

/**
 *
 * @author root
 */
public class Projects {

    private ConnectToDb s_con = null;
    private ConnectToDb t_con = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private String s_db = "";
    private String t_db = "";
    private String project = "";
    private IdChanges idch;
    private static Logger logger = Logger.getLogger(Projects.class);

    public Projects(ConnectToDb s_con, ConnectToDb t_con, String s_db, String t_db, String project) throws Exception {
        this.s_con = s_con;
        this.t_con = t_con;
        this.s_db = s_db;
        this.t_db = t_db;
        this.project = project;
        idch = IdChanges.getInstance();

    }

    public String getSourceInfo() {
        String result = "";
        try {
            stmt = s_con.getCon().createStatement();
            //      System.out.println("SELECT * FROM " + s_db + ".projects WHERE name=\"" + project + "\"");
            rs = stmt.executeQuery("SELECT * FROM " + s_db + ".projects WHERE name=\"" + project + "\"");
            while (rs.next()) {
                result += "id = " + rs.getString("id");
                result += "\nname = " + rs.getString("name");
                result += "\ndescription = " + rs.getString("description");
                result += "\nhomepage = " + rs.getString("homepage");
                result += "\nis_public = " + rs.getString("is_public");
                result += "\nparent_id = " + rs.getString("parent_id");
                result += "\ncreated_on = " + rs.getString("created_on");
                result += "\nupdated_on = " + rs.getString("updated_on");
                result += "\nidentifier = " + rs.getString("identifier");
                result += "\nstatus = " + rs.getString("status");
                result += "\nlft = " + rs.getString("lft");
                result += "\nrgt = " + rs.getString("rgt");

                //save old id:
                idch.getProjectID().get(0).set(0, rs.getInt("id"));
            }
        } catch (Exception e) {
            logger.error("in getSourceInfo", e);
            //e.printStackTrace();
        }
        return result;
    }

    private int getNewID() {
        int res = 0;
        try {
            stmt = t_con.getCon().createStatement();
            ResultSet rsid = stmt.executeQuery("SELECT MAX(a.id) AS max_id FROM " + t_db + ".projects a");
            while (rsid.next()) {
                res = rsid.getInt("max_id");
            }
        } catch (Exception e) {
            logger.error("in getNewID", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
        return res;
    }

    private int getMaxLft() {
        int res = 0;
        try {
            stmt = t_con.getCon().createStatement();
            ResultSet rsid = stmt.executeQuery("SELECT MAX(a.lft)+1 AS max_lft FROM " + t_db + ".projects a");
            while (rsid.next()) {
                res = rsid.getInt("max_lft");
            }
        } catch (Exception e) {
            logger.error("in getMaxLft", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
        return res;
    }

    private int getMaxRgt() {
        int res = 0;
        try {
            stmt = t_con.getCon().createStatement();
            ResultSet rsid = stmt.executeQuery("SELECT MAX(a.rgt)+1 AS max_rgt FROM " + t_db + ".projects a");
            while (rsid.next()) {
                res = rsid.getInt("max_rgt");
            }
        } catch (Exception e) {
            logger.error("in getMaxRgt", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }

        return res;
    }

    public String getTargetInfo() {
        String result = "";
        try {
            stmt = s_con.getCon().createStatement();
            //   System.out.println("SELECT * FROM " + s_db + ".projects WHERE name=\"" + project + "\"");
            rs = stmt.executeQuery("SELECT * FROM " + s_db + ".projects WHERE name=\"" + project + "\"");
            while (rs.next()) {
                result += "id = " + (getNewID() + 1) + " или больше, если до этого были удаления";
                result += "\nname = " + rs.getString("name");
                result += "\ndescription = " + rs.getString("description");
                result += "\nhomepage = " + rs.getString("homepage");
                result += "\nis_public = " + rs.getString("is_public");
                result += "\nparent_id = " + rs.getString("parent_id");
                result += "\ncreated_on = " + rs.getString("created_on");
                result += "\nupdated_on = " + rs.getString("updated_on");
                result += "\nidentifier = " + rs.getString("identifier");
                result += "\nstatus = " + rs.getString("status");
                result += "\nlft = " + (this.getMaxLft() + 1);
                result += "\nrgt = " + (this.getMaxRgt() + 1);
            }
        } catch (Exception e) {
            logger.error("in getTargetInfo", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
        return result;
    }

    private ArrayList projectForReport(String name, String db) {
        ArrayList res = new ArrayList(0);
        try {
            stmt = s_con.getCon().createStatement();

            String query = "SELECT * FROM " + db + ".projects u WHERE name = \'" + name + "\'";
            //   System.out.println(query);
            ResultSet myrs = stmt.executeQuery(query);
            if (myrs.next()) {
                res.add(myrs.getString("name"));
                res.add(myrs.getString("identifier"));
            }
        } catch (Exception e) {
            logger.error("in projectForReport", e);
            //e.printStackTrace();
        }

        return res;
    }

    //return  new id in target DB
    public int ProjectMove() {
        ArrayList<ArrayList> report = new ArrayList<ArrayList>(0);
        report.add(new ArrayList(0));

        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("Project");
        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("old_NAME");
        report.get(report.size() - 1).add("old_IDENTIFIER");
        report.get(report.size() - 1).add("STATUS");
        report.get(report.size() - 1).add("new_NAME");
        report.get(report.size() - 1).add("new_IDENTIFIER");


        String prj_name = "";
        String description = "";
        String homepage = "";
        boolean is_public = true;
        String parent_id = "";
        String created_on = "";
        String updated_on = "";
        String identifier = "";

        int status = 0;
        int lft = 0;
        int rgt = 0;

        try {
            stmt = s_con.getCon().createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + s_db + ".projects WHERE name=\"" + project + "\"");
            while (rs.next()) {
                prj_name = "\"" + rs.getString("name") + "\"";
                description = "\"" + rs.getString("description") + "\"";
                homepage = "\"" + rs.getString("homepage") + "\"";
                is_public = rs.getBoolean("is_public");
                parent_id = rs.getString("parent_id");
                created_on = "\'" + rs.getString("created_on") + "\'";
                updated_on = "\'" + rs.getString("updated_on") + "\'";
                identifier = "\"" + rs.getString("identifier") + "\"";
                status = rs.getInt("status");
                lft = (this.getMaxLft() + 1);
                rgt = (this.getMaxRgt() + 1);
            }

            //INSERT into target:
            stmt = t_con.getCon().createStatement();

            String query = "INSERT INTO " + t_db + ".projects"
                    + "(name, description, homepage, is_public, parent_id, created_on, updated_on, identifier, status, lft, rgt)"
                    + " VALUES(" + prj_name + "," + description + "," + homepage + "," + is_public + ", NULL," + created_on + ","
                    + updated_on + "," + identifier + "," + status + "," + lft + "," + rgt + ")";

            //   System.out.println(query);

            stmt.execute(query);

            //save new id:
            idch.getProjectID().get(0).set(1, this.getNewID());

            report.add(new ArrayList(0));
            ArrayList lst = projectForReport(project, s_db);
            report.get(report.size() - 1).add(lst.get(0));
            report.get(report.size() - 1).add(lst.get(1));
            report.get(report.size() - 1).add("AS_NEW");
            lst = projectForReport(project, t_db);
            report.get(report.size() - 1).add(lst.get(0));
            report.get(report.size() - 1).add(lst.get(1));

            idch.addToReportPrj(report);
        } catch (Exception e) {
            logger.error("in ProjectMove", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
        return this.getNewID();
    }
}
