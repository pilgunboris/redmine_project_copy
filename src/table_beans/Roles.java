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
public class Roles {

    private ConnectToDb s_con = null;
    private ConnectToDb t_con = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private String s_db = "";
    private String t_db = "";
    private IdChanges idch = null;
    private static Logger logger = Logger.getLogger(Roles.class);

    public Roles(ConnectToDb s_con, ConnectToDb t_con, String s_db, String t_db) throws Exception {
        this.s_con = s_con;
        this.t_con = t_con;
        this.s_db = s_db;
        this.t_db = t_db;
        idch = IdChanges.getInstance();
    }

    public ArrayList GetRoles(String dbName) {
        ArrayList res = new ArrayList(0);
        //numbers in the result
        String name = "";            //num = 0

        try {
            stmt = s_con.getCon().createStatement();
            String query = "";
            query = "SELECT * FROM " + dbName + ".roles";

            //         System.out.println(query);
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                name = rs.getString("name");
                if (name.compareTo("") != 0) {
                    res.add(name);
                    //   res.add(res)
                }
            }
        } catch (Exception e) {
            logger.error("in GetRoles", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
        return res;
    }

    public String GetRoleInfo(String name, String db) {
        String res = "";
        try {
            stmt = s_con.getCon().createStatement();
            String query = "SELECT * FROM " + db + ".roles u WHERE name = \"" + name + "\"";

//            System.out.println(query);


            rs = stmt.executeQuery(query);
            while (rs.next()) {
                res += "id = " + rs.getString("id");
                res += "\nname = " + rs.getString("name");
                res += "\nposition = " + rs.getString("position");
                res += "\nassignable = " + rs.getString("assignable");
                res += "\nbuiltin = " + rs.getBoolean("builtin");
                res += "\npermissions = " + rs.getBoolean("permissions");
            }
        } catch (Exception e) {
            logger.error("in GetRoleInfo", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
        return res;
    }

    public ArrayList<ArrayList> CreateRolesRelations() {
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);

        ArrayList s_roles = this.GetRoles(s_db);
        ArrayList t_roles = this.GetRoles(t_db);
        try {
            for (int i = 0; i < s_roles.size(); ++i) {
                String src = s_roles.get(i).toString();
                if (src.compareTo("") == 0) {
                    continue;
                }

                res.add(new ArrayList(0));
                res.get(i).add(src);
                for (int j = 0; j < t_roles.size(); ++j) {
                    String trg = t_roles.get(j).toString();
                    if (trg.compareTo(src) == 0) {
                        res.get(i).add(trg);
                    }
                }
                if (res.get(i).size() == 1) {
                    res.get(i).add("new_record");
                }
            }
        } catch (Exception e) {
            logger.error("in CreateRolesRelations", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
        return res;
    }

    public ArrayList<ArrayList> MoveValues(ArrayList<ArrayList> valRelat) {
        ArrayList<ArrayList> report = new ArrayList<ArrayList>(0);
        report.add(new ArrayList(0));

        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("Roles");
        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("old_NAME");
        report.get(report.size() - 1).add("STATUS");
        report.get(report.size() - 1).add("new_NAME");

        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);

        String name = "";
        int position = 0;
        boolean assignable = true;
        int builtin = 0;
        String permissions = "";

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
                    String query = "SELECT * FROM " + s_db + ".roles u WHERE name = \"" + src + "\"";

//                    System.out.println(query);

                    rs = stmt.executeQuery(query);

                    //add old id to res

                    if (rs.next()) {
                        res.get(res.size() - 1).add(rs.getString("id"));

                        name = "\"" + rs.getString("name") + "\"";
                        assignable = rs.getBoolean("assignable");
                        builtin = rs.getInt("builtin");
                        permissions = "\"" + rs.getString("permissions") + "\"";
                    }
//find max from target positions
                    query = "SELECT MAX(position)+1 AS max_pos FROM " + t_db + ".roles";

//                    System.out.println(query);

                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        position = rs.getInt("max_pos");
                    }
//insert into target DB
                    stmt = t_con.getCon().createStatement();
                    query = "INSERT INTO " + t_db + ".roles(name, position, assignable, builtin, permissions) "
                            + "VALUES(" + name + ", " + position + ", " + assignable + ", " + builtin + ", " + permissions + ")";

//                    System.out.println(query);
                    stmt.executeUpdate(query);

                    //set the new id of user in target db
                    query = "SELECT MAX(u.id) AS new_id FROM " + t_db + ".users u";
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        res.get(res.size() - 1).add(rs.getString("new_id"));
                    }

                } else {
                    stmt = s_con.getCon().createStatement();
                    String query = "SELECT r.id FROM " + s_db + ".roles r WHERE name = \"" + src + "\"";

//                    System.out.println(query);

                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        //set old id from source DB
                        res.get(res.size() - 1).add(rs.getString("id"));
                    }

                    stmt = t_con.getCon().createStatement();
                    query = "SELECT r.id FROM " + t_db + ".roles r WHERE name = \"" + trg + "\"";

                    //System.out.println(query);

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
