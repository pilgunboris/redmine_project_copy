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
public class Users {

    private ConnectToDb s_con = null;
    private ConnectToDb t_con = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private String s_db = "";
    private String t_db = "";
    private String project = "";
    private IdChanges idch = null;
    private static Logger logger = Logger.getLogger(Users.class);

    public Users(ConnectToDb s_con, ConnectToDb t_con, String s_db, String t_db, String project) throws Exception {
        this.s_con = s_con;
        this.t_con = t_con;
        this.s_db = s_db;
        this.t_db = t_db;
        this.project = project;
        idch = IdChanges.getInstance();
    }

    public Users(ConnectToDb s_con, ConnectToDb t_con, String s_db, String t_db) throws Exception {
        this.s_con = s_con;
        this.t_con = t_con;
        this.s_db = s_db;
        this.t_db = t_db;
        idch = IdChanges.getInstance();
    }

    /**
     *
     * @param OnlyForProject set what users will searchable
     * @return ArrayList<ArrayList> with 4 values in each row
     */
    public ArrayList<ArrayList> GetSourceUsers(boolean OnlyForProject) {
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        //numbers in the result
        String mail = "";            //num = 0
        String login = "";          //num = 1
        String firstname = "";      //num = 2
        String lastname = "";       //num = 3

        try {
            stmt = s_con.getCon().createStatement();
            String query = "";
            if (OnlyForProject) {
                query = "SELECT * FROM " + s_db + ".users u WHERE u.id "
                        + "IN(SELECT m.user_id FROM " + s_db + ".members m WHERE project_id "
                        + "IN(SELECT p.id FROM " + s_db + ".projects p WHERE name = \"" + project + "\"))";
            } else {
                query = "SELECT * FROM " + s_db + ".users u ORDER BY u.mail";
            }
//            System.out.println(query);
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                mail = rs.getString("mail").toLowerCase();
                login = rs.getString("login").toLowerCase();
                firstname = rs.getString("firstname").toLowerCase();
                lastname = rs.getString("lastname").toLowerCase();
                if (mail.compareTo("") != 0) {
                    res.add(new ArrayList(0));
                    res.get(res.size() - 1).add(mail);
                    res.get(res.size() - 1).add(login);
                    res.get(res.size() - 1).add(firstname);
                    res.get(res.size() - 1).add(lastname);
                    //   res.add(res)
                }
            }
        } catch (Exception e) {
            logger.error("in GetSourceUsers", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
        return res;
    }

    //return all users from target DB
    public ArrayList<ArrayList> GetTargetUsers() {
        String mail = "";           //num = 0
        String login = "";          //num = 1
        String firstname = "";      //num = 2
        String lastname = "";       //num = 3
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        try {
            stmt = s_con.getCon().createStatement();
            String query = "SELECT * FROM " + t_db + ".users ORDER BY mail";

//            System.out.println(query);
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                mail = rs.getString("mail").toLowerCase();
                login = rs.getString("login").toLowerCase();
                firstname = rs.getString("firstname").toLowerCase();
                lastname = rs.getString("lastname").toLowerCase();
                if (mail.compareTo("") != 0) {
                    res.add(new ArrayList(0));
                    res.get(res.size() - 1).add(mail);
                    res.get(res.size() - 1).add(login);
                    res.get(res.size() - 1).add(firstname);
                    res.get(res.size() - 1).add(lastname);
                    //   res.add(res)
                }

            }
        } catch (Exception e) {
            logger.error("in GetTargetUsers", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
        return res;
    }

    /**
     *
     * @param user_mail
     * @param db
     * @return full user information from DB setted in @param db
     */
    public String GetUserInfo(String user_mail, String db) {
        String res = "";
        try {
            stmt = s_con.getCon().createStatement();
            String query = "SELECT * FROM " + db + ".users u WHERE mail = \"" + user_mail + "\"";

//            System.out.println(query);

            rs = stmt.executeQuery(query);
            while (rs.next()) {
                res += "login = " + rs.getString("login");
                res += "\nhashed_password = " + rs.getString("hashed_password");
                res += "\nfirstname = " + rs.getString("firstname");
                res += "\nlastname = " + rs.getString("lastname");
                res += "\nmail = " + rs.getString("mail");
                res += "\nmail_notification = " + rs.getBoolean("mail_notification");
                res += "\nadmin = " + rs.getBoolean("admin");
                res += "\nstatus = " + rs.getInt("status");
                res += "\nlast_login_on = " + rs.getString("last_login_on");
                res += "\nlanguage = " + rs.getString("language");
                res += "\nauth_source_id = " + rs.getInt("auth_source_id");
                res += "\ncreated_on = " + rs.getString("created_on");
                res += "\nupdated_on = " + rs.getString("updated_on");
                res += "\ntype = " + rs.getString("type");
                res += "\nidentity_url = " + rs.getString("identity_url");
            }
        } catch (Exception e) {
            logger.error("in GetUserInfo", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
        return res;
    }

    public ArrayList<ArrayList> CreateUsersRelations(boolean OnlyForProject) {
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);

        ArrayList<ArrayList> s_users = this.GetSourceUsers(OnlyForProject);
        ArrayList<ArrayList> t_users = this.GetTargetUsers();
        try {
            for (int i = 0; i < s_users.size(); ++i) {
                String src_mail = s_users.get(i).get(0).toString();
                String src_login = s_users.get(i).get(1).toString();
                String src_f_name = s_users.get(i).get(2).toString();
                String src_l_name = s_users.get(i).get(3).toString();
                if (src_mail.compareTo("") == 0) {
                    continue;
                }

                res.add(new ArrayList(0));
                res.get(i).add(src_mail);
                for (int j = 0; j < t_users.size(); ++j) {
                    String trg_mail = t_users.get(j).get(0).toString();
                    String trg_login = t_users.get(j).get(1).toString();
                    String trg_f_name = t_users.get(j).get(2).toString();
                    String trg_l_name = t_users.get(j).get(3).toString();
                    if (trg_login.compareTo(src_login) == 0) {
                        res.get(i).add(trg_mail);
                    } else {
                        if ((trg_f_name.compareTo(src_f_name) == 0) && (trg_l_name.compareTo(src_l_name) == 0)) {
                            res.get(i).add(trg_mail);
                        } else {
                            if (trg_mail.compareTo(src_mail) == 0) {
                                res.get(i).add(trg_mail);
                            }
                        }
                    }
                }
                if (res.get(i).size() == 1) {
                    res.get(i).add("new_record");
                }
            }
        } catch (Exception e) {
            logger.error("in CreateUsersRelations", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
        return res;
    }

    private ArrayList userForReport(String mail, String db) {
        ArrayList res = new ArrayList(0);
        try {
            stmt = s_con.getCon().createStatement();

            String query = "SELECT * FROM " + db + ".users u WHERE mail = \'" + mail + "\'";
//            System.out.println(query);
            ResultSet myrs = stmt.executeQuery(query);
            if (myrs.next()) {
                res.add(myrs.getString("login"));
                res.add(myrs.getString("firstname"));
                res.add(myrs.getString("lastname"));
                res.add(mail);
            }
        } catch (Exception e) {
            logger.error("in userForReport", e);
            //e.printStackTrace();
        }

        return res;
    }

    /**
     * move values from source base to target by assigned with valRelat (mails of users)
     * returns relations between old and new ids
     */
    public ArrayList<ArrayList> MoveValues(ArrayList<ArrayList> valRelat) {
        ArrayList<ArrayList> report = new ArrayList<ArrayList>(0);
        report.add(new ArrayList(0));

        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("Users");
        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("old_LOGIN");
        report.get(report.size() - 1).add("old_FIRSTNAME");
        report.get(report.size() - 1).add("old_LASTNAME");
        report.get(report.size() - 1).add("old_MAIL");
        report.get(report.size() - 1).add("STATUS");
        report.get(report.size() - 1).add("new_LOGIN");
        report.get(report.size() - 1).add("new_FIRSTNAME");
        report.get(report.size() - 1).add("new_LASTNAME");
        report.get(report.size() - 1).add("new_MAIL");

        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);

        String login = "";
        String hashed_password = "";
        String firstname = "";
        String lastname = "";
        String mail = "";
        boolean mail_notification = true;
        boolean admin = false;
        int status = 1;
        String last_login_on = "";  //datetime
        String language = "";
        String auth_source_id = "NULL";
        String created_on = "";
        String updated_on = "";
        String type = "";
        String identity_url = "";

        try {
            for (int i = 0; i < valRelat.size(); ++i) {
                report.add(new ArrayList(0));
                String src = valRelat.get(i).get(0).toString();
                String trg = valRelat.get(i).get(1).toString();
                if (trg.compareTo("ignore") == 0) {
                    ArrayList lst = userForReport(src, s_db);
                    report.get(report.size() - 1).add(lst.get(0));
                    report.get(report.size() - 1).add(lst.get(1));
                    report.get(report.size() - 1).add(lst.get(2));
                    report.get(report.size() - 1).add(lst.get(3));
                    report.get(report.size() - 1).add("IGNORED");
                    continue;
                }
                res.add(new ArrayList(0));
                if (trg.compareTo("new_record") == 0) {
                    ArrayList lst = userForReport(src, s_db);
                    report.get(report.size() - 1).add(lst.get(0));
                    report.get(report.size() - 1).add(lst.get(1));
                    report.get(report.size() - 1).add(lst.get(2));
                    report.get(report.size() - 1).add(lst.get(3));
                    report.get(report.size() - 1).add("AS_NEW");

                    stmt = s_con.getCon().createStatement();
                    String query = "SELECT * FROM " + s_db + ".users u WHERE mail = \"" + src + "\"";

//                    System.out.println(query);

                    rs = stmt.executeQuery(query);

                    //add old id to res

                    if (rs.next()) {
                        res.get(res.size() - 1).add(rs.getString("id"));

                        login = "\"" + rs.getString("login") + "\"";
                        hashed_password = "\"" + rs.getString("hashed_password") + "\"";
                        firstname = "\"" + rs.getString("firstname") + "\"";
                        lastname = "\"" + rs.getString("lastname") + "\"";
                        mail = "\"" + rs.getString("mail") + "\"";
                        mail_notification = rs.getBoolean("mail_notification");
                        admin = rs.getBoolean("admin");
                        status = rs.getInt("status");
                        String tmp_last_login_on = rs.getString("last_login_on");
                        last_login_on = ((tmp_last_login_on == null) ? "null" : ("\'" + tmp_last_login_on + "\'"));
                        language = "\"" + rs.getString("language") + "\"";
                        auth_source_id = "NULL";//rs.getInt("auth_source_id");
                        created_on = "\'" + rs.getString("created_on") + "\'";
                        updated_on = "\'" + rs.getString("updated_on") + "\'";
                        type = "\"" + rs.getString("type") + "\"";
                        identity_url = ((rs.getString("identity_url") != null) ? ("\"" + rs.getString("identity_url") + "\"") : "NULL");
                    }

                    stmt = t_con.getCon().createStatement();
                    query = "INSERT INTO " + t_db + ".users(login, hashed_password, "
                            + "firstname, lastname, mail, mail_notification, admin, status, last_login_on,"
                            + "language, auth_source_id, created_on, updated_on, type, idreport.get(report.size() - 1).add(lst.get(3));entity_url) "
                            + "VALUES(" + login + ", " + hashed_password + ", " + firstname + ", " + lastname + ", "
                            + mail + ", " + mail_notification + ", " + admin + ", " + status + ", " + last_login_on + ","
                            + language + ", " + auth_source_id + ", " + created_on + "," + updated_on + "," + type + ","
                            + identity_url + ")";


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
                    String query = "SELECT u.id FROM " + s_db + ".users u WHERE mail = \"" + src + "\"";

//                    System.out.println(query);

                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        //set old id from source DB
                        res.get(res.size() - 1).add(rs.getString("id"));
                    }

                    stmt = t_con.getCon().createStatement();
                    query = "SELECT u.id FROM " + t_db + ".users u WHERE mail = \"" + trg + "\"";

//                    System.out.println(query);
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        res.get(res.size() - 1).add(rs.getString("id"));
                    }
                    ArrayList lst = userForReport(src, s_db);
                    report.get(report.size() - 1).add(lst.get(0));
                    report.get(report.size() - 1).add(lst.get(1));
                    report.get(report.size() - 1).add(lst.get(2));
                    report.get(report.size() - 1).add(lst.get(3));
                    report.get(report.size() - 1).add("RELATED");
                    lst = userForReport(trg, t_db);
                    report.get(report.size() - 1).add(lst.get(0));
                    report.get(report.size() - 1).add(lst.get(1));
                    report.get(report.size() - 1).add(lst.get(2));
                    report.get(report.size() - 1).add(lst.get(3));
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

    /**
     *
     * @param usrRelat ArrayList of old/new users ids
     * @param prjRelat ArrayList of old/new projects ids
     * @return ArrayList<ArrayList> relations of members for using in member_roles moving
     */
    public ArrayList<ArrayList> moveMembers(ArrayList<ArrayList> usrRelat, ArrayList<ArrayList> prjRelat) {
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        String old_prj = prjRelat.get(0).get(0).toString();
        String new_prj = prjRelat.get(0).get(1).toString();
        try {
            Statement s_stmt = s_con.getCon().createStatement();
            String query = "SELECT * FROM " + s_db + ".members WHERE project_id = " + old_prj;
            ResultSet tmp_rs = s_stmt.executeQuery(query);
            stmt = t_con.getCon().createStatement(); //target base

            while (tmp_rs.next()) {
                res.add(new ArrayList(0));
                int old_usr = tmp_rs.getInt("user_id");
                int new_usr = idch.evaluateID(usrRelat, old_usr);

                String s_query = "SELECT * FROM " + s_db + ".members WHERE user_id = " + old_usr
                        + " AND project_id = " + old_prj;
//                System.out.println(s_query);
                Statement st_select = s_con.getCon().createStatement();
                rs = st_select.executeQuery(s_query);

                if (rs.next()) {
                    res.get(res.size() - 1).add(rs.getString("id")); //add old id
//                    System.out.println("old id members = " + res.get(res.size() - 1).get(0));

                    String t_query = "INSERT INTO " + t_db + ".members(user_id, project_id, created_on, mail_notification) "
                            + "VALUES(" + new_usr + ", " + new_prj + ", \'" + rs.getString("created_on") + "\', "
                            + rs.getBoolean("mail_notification") + ")";
//                    System.out.println(t_query);
                    stmt.executeUpdate(t_query); //???
                }
                query = "SELECT MAX(m.id) AS last_id FROM " + t_db + ".members m";
                ResultSet max_rs = stmt.executeQuery(query);
                if (max_rs.next()) {
                    res.get(res.size() - 1).add(max_rs.getInt("last_id")); //adding new id
//                    System.out.println("new id members = " + res.get(res.size() - 1).get(1));
                }
            }
        } catch (Exception e) {
            logger.error("in moveMembers", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
        return res;
    }

    public ArrayList<ArrayList> moveMemberRoles(ArrayList<ArrayList> memberID, ArrayList<ArrayList> roleID) {
        ArrayList<ArrayList> res = new ArrayList<ArrayList>(0);
        int member_id = 0;
        int role_id = 0;
        String inherited_from = "NULL";
        try {
            Statement s_stmt = s_con.getCon().createStatement();
            stmt = t_con.getCon().createStatement(); //target base
            for (int i = 0; i < memberID.size(); ++i) {
                String old_mem = memberID.get(i).get(0).toString();
                String new_mem = memberID.get(i).get(1).toString();

                String s_query = "SELECT * FROM " + s_db + ".member_roles m WHERE member_id = " + old_mem;
//                System.out.println(s_query);
                rs = s_stmt.executeQuery(s_query);
                while (rs.next()) {
                    res.add(new ArrayList(0));
                    res.get(res.size() - 1).add(rs.getInt("id"));//adding old id
//                    System.out.println("old id member_roles = " + res.get(0).get(0));


                    member_id = idch.evaluateID(memberID, rs.getInt("member_id"));
                    role_id = idch.evaluateID(roleID, rs.getInt("role_id"));
                    inherited_from = (rs.getString("inherited_from") == null) ? ("NULL") : (idch.evaluateID(idch.getMember_rolesID(), rs.getInt("inherited_from")) + "");

                    String t_query = "INSERT INTO " + t_db + ".member_roles(member_id, role_id, inherited_from) "
                            + "VALUES(" + member_id + ", " + role_id + ", " + inherited_from + ")";
//                    System.out.println(t_query);
                    stmt.executeUpdate(t_query);
                    String query = "SELECT MAX(m.id) AS last_id FROM " + t_db + ".member_roles m";
                    ResultSet tmp_rs = stmt.executeQuery(query);
                    if (tmp_rs.next()) {
                        res.get(res.size() - 1).add(tmp_rs.getInt("last_id")); //adding new id
//                        System.out.println("new id member_roles = " + res.get(res.size() - 1).get(1));
                    }
                }

            }
        } catch (Exception e) {
            logger.error("in moveMemberRoles", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }

        return res;
    }
}
