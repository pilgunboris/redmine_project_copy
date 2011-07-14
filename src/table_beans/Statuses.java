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
public class Statuses {

    private ConnectToDb s_con = null;
    private ConnectToDb t_con = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private String s_db = "";
    private String t_db = "";
    private IdChanges idch = null;
    private static Logger logger = Logger.getLogger(Statuses.class);

    public Statuses(ConnectToDb s_con, ConnectToDb t_con, String s_db, String t_db) throws Exception {
        this.s_con = s_con;
        this.t_con = t_con;
        this.s_db = s_db;
        this.t_db = t_db;
        idch = IdChanges.getInstance();
    }

    public ArrayList GetStatuses(String dbName) {
        ArrayList res = new ArrayList(0);
        //numbers in the result
        String name = "";            //num = 0

        try {
            stmt = s_con.getCon().createStatement();
            String query = "";
            query = "SELECT * FROM " + dbName + ".issue_statuses";

            //        System.out.println(query);
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                name = rs.getString("name");
                if (name.compareTo("") != 0) {
                    res.add(name);
                    //   res.add(res)
                }
            }
        } catch (Exception e) {
            logger.error("in GetStatuses", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
        return res;
    }

    public String GetStatusInfo(String name, String db) {
        String res = "";
        try {
            stmt = s_con.getCon().createStatement();
            String query = "SELECT * FROM " + db + ".issue_statuses u WHERE name = \"" + name + "\"";

            //        System.out.println(query);


            rs = stmt.executeQuery(query);
            while (rs.next()) {
                res += "id = " + rs.getString("id");
                res += "\nname = " + rs.getString("name");
                res += "\nis_closed = " + rs.getString("is_closed");
                res += "\nis_default = " + rs.getString("is_default");
                res += "\nposition = " + rs.getString("position");
                res += "\ndefault_done_ratio = " + rs.getString("default_done_ratio");
            }
        } catch (Exception e) {
            logger.error("in GetStatusInfo", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
        return res;
    }

    public ArrayList<ArrayList> CreateStatusesRelations() {
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);

        ArrayList s_statuses = this.GetStatuses(s_db);
        ArrayList t_statuses = this.GetStatuses(t_db);
        try {
            for (int i = 0; i < s_statuses.size(); ++i) {
                String src = s_statuses.get(i).toString();
                if (src.compareTo("") == 0) {
                    continue;
                }

                res.add(new ArrayList(0));
                res.get(i).add(src);
                for (int j = 0; j < t_statuses.size(); ++j) {
                    String trg = t_statuses.get(j).toString();
                    if (trg.compareTo(src) == 0) {
                        res.get(i).add(trg);
                    }
                }
                if (res.get(i).size() == 1) {
                    res.get(i).add("new_record");
                }
            }
        } catch (Exception e) {
            logger.error("in CreateStatusesRelations", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
        return res;
    }

    public ArrayList<ArrayList> MoveValues(ArrayList<ArrayList> valRelat) {
        ArrayList<ArrayList> report = new ArrayList<ArrayList>(0);
        report.add(new ArrayList(0));

        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("IssueStatuses");
        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("old_NAME");
        report.get(report.size() - 1).add("STATUS");
        report.get(report.size() - 1).add("new_NAME");


        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);

        String name = "";
        boolean is_closed = false;
        boolean is_default = false;
        int position = 0;
        String default_done_ratio = "";


        try {
            for (int i = 0; i < valRelat.size(); ++i) {
                report.add(new ArrayList(0));
                String src = valRelat.get(i).get(0).toString();
                String trg = valRelat.get(i).get(1).toString();
                if (trg.compareTo("ignore") == 0) {
                    report.get(report.size() - 1).add(src);
                    report.get(report.size() - 1).add("IGNORED");

                    continue;
                }
                res.add(new ArrayList(0));
                if (trg.compareTo("new_record") == 0) {
                    report.get(report.size() - 1).add(src);
                    report.get(report.size() - 1).add("AS_NEW");


                    stmt = s_con.getCon().createStatement();
                    String query = "SELECT * FROM " + s_db + ".issue_statuses u WHERE name = \"" + src + "\"";

                    //      System.out.println(query);

                    rs = stmt.executeQuery(query);

                    //add old id to res

                    if (rs.next()) {
                        res.get(res.size() - 1).add(rs.getString("id"));

                        name = "\"" + rs.getString("name") + "\"";
                        is_closed = rs.getBoolean("is_closed");
                        is_default = rs.getBoolean("is_default");
                        default_done_ratio = ((rs.getString("default_done_ratio") != null) ? ("\"" + rs.getString("default_done_ratio") + "\"") : "NULL");
                    }
//find max from target positions
                    query = "SELECT MAX(position)+1 AS max_pos FROM " + t_db + ".issue_statuses";

                    //      System.out.println(query);

                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        position = rs.getInt("max_pos");
                    }
//insert into target DB
                    stmt = t_con.getCon().createStatement();
                    query = "INSERT INTO " + t_db + ".issue_statuses(name, is_closed, is_default, position, default_done_ratio) "
                            + "VALUES(" + name + ", " + is_closed + ", " + is_default + ", " + position + ", " + default_done_ratio + ")";

                    //       System.out.println(query);
                    stmt.executeUpdate(query);

                    //set the new id of user in target db
                    query = "SELECT MAX(u.id) AS new_id FROM " + t_db + ".issue_statuses u";
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        res.get(res.size() - 1).add(rs.getString("new_id"));
                    }

                } else {
                    stmt = s_con.getCon().createStatement();
                    String query = "SELECT r.id FROM " + s_db + ".issue_statuses r WHERE name = \"" + src + "\"";

                    //      System.out.println(query);

                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        //set old id from source DB
                        res.get(res.size() - 1).add(rs.getString("id"));
                    }

                    stmt = t_con.getCon().createStatement();
                    query = "SELECT r.id FROM " + t_db + ".issue_statuses r WHERE name = \"" + trg + "\"";

                    //       System.out.println(query);

                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        res.get(res.size() - 1).add(rs.getString("id"));
                    }
                    report.get(report.size() - 1).add(src);
                    report.get(report.size() - 1).add("RELATED");
                    report.get(report.size() - 1).add(trg);
                }
            }

            idch.addToReportE(report);
        } catch (Exception e) {
            logger.error("in MoveValues", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
        return res;
    }
}
