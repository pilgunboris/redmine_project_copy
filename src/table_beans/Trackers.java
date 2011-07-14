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
public class Trackers {

    private ConnectToDb s_con = null;
    private ConnectToDb t_con = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private String s_db = "";
    private String t_db = "";
    private IdChanges idch = null;
    private static Logger logger = Logger.getLogger(Trackers.class);

    public Trackers(ConnectToDb s_con, ConnectToDb t_con, String s_db, String t_db) throws Exception {
        this.s_con = s_con;
        this.t_con = t_con;
        this.s_db = s_db;
        this.t_db = t_db;
        idch = IdChanges.getInstance();
    }

    public ArrayList GetTrackers(String dbName) {
        ArrayList res = new ArrayList(0);
        //numbers in the result
        String name = "";            //num = 0

        try {
            stmt = s_con.getCon().createStatement();
            String query = "";
            query = "SELECT * FROM " + dbName + ".trackers";

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
            logger.error("in GetTrackers", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
        return res;
    }

    public String GetTrackerInfo(String name, String db) {
        String res = "";
        try {
            stmt = s_con.getCon().createStatement();
            String query = "SELECT * FROM " + db + ".trackers u WHERE name = \"" + name + "\"";

//            System.out.println(query);


            rs = stmt.executeQuery(query);
            while (rs.next()) {
                res += "id = " + rs.getString("id");
                res += "\nname = " + rs.getString("name");
                res += "\nis_in_chlog = " + rs.getString("is_in_chlog");
                res += "\nposition = " + rs.getString("position");
                res += "\nis_in_roadmap = " + rs.getBoolean("is_in_roadmap");
            }
        } catch (Exception e) {
            logger.error("in GetTrackerInfo", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
        return res;
    }

    public ArrayList<ArrayList> CreateTrackersRelations() {
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);

        ArrayList s_trackers = this.GetTrackers(s_db);
        ArrayList t_trackers = this.GetTrackers(t_db);
        try {
            for (int i = 0; i < s_trackers.size(); ++i) {
                String src = s_trackers.get(i).toString();
                if (src.compareTo("") == 0) {
                    continue;
                }

                res.add(new ArrayList(0));
                res.get(i).add(src);
                for (int j = 0; j < t_trackers.size(); ++j) {
                    String trg = t_trackers.get(j).toString();
                    if (trg.compareTo(src) == 0) {
                        res.get(i).add(trg);
                    }
                }
                if (res.get(i).size() == 1) {
                    res.get(i).add("new_record");
                }
            }
        } catch (Exception e) {
            logger.error("in CreateTrackersRelations", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
        return res;
    }

    public ArrayList<ArrayList> MoveValues(ArrayList<ArrayList> valRelat) {

        ArrayList<ArrayList> report = new ArrayList<ArrayList>(0);
        report.add(new ArrayList(0));

        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("Trackers");
        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("old_NAME");
        report.get(report.size() - 1).add("STATUS");
        report.get(report.size() - 1).add("new_NAME");


        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);

        String name = "";
        boolean is_in_chlog = true;
        int position = 0;
        boolean is_in_roadmap = true;

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
                    String query = "SELECT * FROM " + s_db + ".trackers u WHERE name = \"" + src + "\"";

//                    System.out.println(query);

                    rs = stmt.executeQuery(query);

                    //add old id to res

                    if (rs.next()) {
                        res.get(res.size() - 1).add(rs.getString("id"));

                        name = "\"" + rs.getString("name") + "\"";
                        is_in_chlog = rs.getBoolean("is_in_chlog");
                        is_in_roadmap = rs.getBoolean("is_in_roadmap");
                    }
//find max from target positions
                    query = "SELECT MAX(position)+1 AS max_pos FROM " + t_db + ".trackers";

//                    System.out.println(query);

                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        position = rs.getInt("max_pos");
                    }
//insert into target DB
                    stmt = t_con.getCon().createStatement();
                    query = "INSERT INTO " + t_db + ".trackers(name, is_in_chlog, position, is_in_roadmap) "
                            + "VALUES(" + name + ", " + is_in_chlog + ", " + position + ", " + is_in_roadmap + ")";

//                    System.out.println(query);
                    stmt.executeUpdate(query);

                    //set the new id of user in target db
                    query = "SELECT MAX(u.id) AS new_id FROM " + t_db + ".trackers u";
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        res.get(res.size() - 1).add(rs.getString("new_id"));
                    }

                } else {
                    stmt = s_con.getCon().createStatement();
                    String query = "SELECT r.id FROM " + s_db + ".trackers r WHERE name = \"" + src + "\"";

//                    System.out.println(query);

                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        //set old id from source DB
                        res.get(res.size() - 1).add(rs.getString("id"));
                    }

                    stmt = t_con.getCon().createStatement();
                    query = "SELECT r.id FROM " + t_db + ".trackers r WHERE name = \"" + trg + "\"";

//                    System.out.println(query);

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
