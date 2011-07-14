/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db_controls;

import com.ibatis.sqlmap.client.SqlMapClient;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import project_copy.IdChanges;
import table_beans.RedmineEnum;

/**
 *
 * @author root
 */
public class EnumControl {

    private static EnumControl instance = null;
    private List<RedmineEnum> L_SDocEnums = null;
    private List<RedmineEnum> L_TDocEnums = null;
    private List<RedmineEnum> L_SPriorEnums = null;
    private List<RedmineEnum> L_TPriorEnums = null;
    private List<RedmineEnum> L_SActEnums = null;
    private List<RedmineEnum> L_TActEnums = null;
    private String s_db;
    private String t_db;
    private IdChanges idch;

    private EnumControl(String s_db, String t_db) {
        try {
            AppSqlConfig sqlConf = AppSqlConfig.getInstance();
            SqlMapClient client = sqlConf.getSqlMapper();
            this.L_SDocEnums = client.queryForList("DefaultEnums", new ParamHelper(s_db, "DocumentCategory"));
            this.L_TDocEnums = client.queryForList("DefaultEnums", new ParamHelper(t_db, "DocumentCategory"));
            this.L_SPriorEnums = client.queryForList("DefaultEnums", new ParamHelper(s_db, "IssuePriority"));
            this.L_TPriorEnums = client.queryForList("DefaultEnums", new ParamHelper(t_db, "IssuePriority"));
            this.L_SActEnums = client.queryForList("DefaultEnums", new ParamHelper(s_db, "TimeEntryActivity"));
            this.L_TActEnums = client.queryForList("DefaultEnums", new ParamHelper(t_db, "TimeEntryActivity"));

            this.s_db = s_db;
            this.t_db = t_db;

            this.idch = IdChanges.getInstance();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static EnumControl getInstance(String s_db, String t_db) {
        if (instance == null) {
            instance = new EnumControl(s_db, t_db);
        }
        return instance;
    }

    public List<RedmineEnum> getL_SActEnums() {
        return L_SActEnums;
    }

    public List<RedmineEnum> getL_SDocEnums() {
        return L_SDocEnums;
    }

    public List<RedmineEnum> getL_SPriorEnums() {
        return L_SPriorEnums;
    }

    public List<RedmineEnum> getL_TActEnums() {
        return L_TActEnums;
    }

    public List<RedmineEnum> getL_TDocEnums() {
        return L_TDocEnums;
    }

    public List<RedmineEnum> getL_TPriorEnums() {
        return L_TPriorEnums;
    }

    public void setL_SActEnums(List<RedmineEnum> L_SActEnums) {
        this.L_SActEnums = L_SActEnums;
    }

    public void setL_SDocEnums(List<RedmineEnum> L_SDocEnums) {
        this.L_SDocEnums = L_SDocEnums;
    }

    public void setL_SPriorEnums(List<RedmineEnum> L_SPriorEnums) {
        this.L_SPriorEnums = L_SPriorEnums;
    }

    public void setL_TActEnums(List<RedmineEnum> L_TActEnums) {
        this.L_TActEnums = L_TActEnums;
    }

    public void setL_TDocEnums(List<RedmineEnum> L_TDocEnums) {
        this.L_TDocEnums = L_TDocEnums;
    }

    public void setL_TPriorEnums(List<RedmineEnum> L_TPriorEnums) {
        this.L_TPriorEnums = L_TPriorEnums;
    }

    /**
     * WARNING!!!!!!!!!!  return the first found the same value
     * @param lre
     * @param name
     * @return
     */
    public RedmineEnum FindByName(List<RedmineEnum> lre, String name) {
        for (int i = 0; i < lre.size(); ++i) {
            try {
                if (lre.get(i).getName().compareTo(name) == 0) {
                    return lre.get(i);
                }
            } catch (Exception ex) {
                Logger.getLogger(EnumControl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
    /*
     * find in all maps of this class from source DB
     */

    private RedmineEnum FindOnSrcByName(String name) {
        RedmineEnum re = null;
        re = FindByName(L_SDocEnums, name);
        if (re == null) {
            re = FindByName(L_SPriorEnums, name);
            if (re == null) {
                re = FindByName(L_SActEnums, name);
            }
        }
        return re;
    }

    private RedmineEnum FindOnTrgByName(String name) {
        RedmineEnum re = null;
        re = FindByName(L_TDocEnums, name);
        if (re == null) {
            re = FindByName(L_TPriorEnums, name);
            if (re == null) {
                re = FindByName(L_TActEnums, name);
            }
        }
        return re;
    }

    public ArrayList<ArrayList> MoveValues(ArrayList<ArrayList> rel) {
        ArrayList<ArrayList> report = new ArrayList<ArrayList>(0);
        report.add(new ArrayList(0));

        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("Enumerations");
        report.add(new ArrayList(0));
        report.get(report.size() - 1).add("old_NAME");
        report.get(report.size() - 1).add("STATUS");
        report.get(report.size() - 1).add("new_NAME");

        ArrayList<ArrayList> ids = new ArrayList<ArrayList>(0);
        try {
            AppSqlConfig iBT = AppSqlConfig.getInstance();
            SqlMapClient client = iBT.getSqlMapper();
            for (int i = 0; i < rel.size(); ++i) {
                report.add(new ArrayList(0));
                String src = rel.get(i).get(0).toString();
                String trg = rel.get(i).get(1).toString();
                if (trg.compareTo("ignore") == 0) {
                    report.get(report.size() - 1).add(src);
                    report.get(report.size() - 1).add("IGNORED");

                    continue;
                }
                if (trg.compareTo("new_record") == 0) {
                    report.get(report.size() - 1).add(src);
                    report.get(report.size() - 1).add("AS_NEW");

                    RedmineEnum re = this.FindOnSrcByName(src);
                    re.setT_db(t_db);
                    client.insert("InsertEnum", re);
                    //select last insert id
                    int id = (Integer) client.queryForObject("LastInsertId", new ParamHelper(t_db, "enumerations"));
                    ids.add(new ArrayList<ArrayList>(0));
                    ids.get(ids.size() - 1).add(re.getId());//set old id
                    ids.get(ids.size() - 1).add(id); //set new id
                } else {
                    int oldID;
                    int newID;
                    oldID = this.FindOnSrcByName(src).getId();
                    newID = this.FindOnTrgByName(trg).getId();
                    ids.add(new ArrayList(0));
                    ids.get(ids.size() - 1).add(oldID);//set old id
                    ids.get(ids.size() - 1).add(newID); //set new id
                    report.get(report.size() - 1).add(src);
                    report.get(report.size() - 1).add("RELATED");
                    report.get(report.size() - 1).add(trg);

                }
            }
            idch.addToReportE(report);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return ids;

    }

    public boolean LoadEnumProperties() {
        try {
            Properties p = new Properties();
            FileInputStream fis = new FileInputStream(new File("enumerations|" + s_db + "|" + t_db + ".properties"));
            p.load(fis);
            ArrayList<ArrayList> loadEnumId = new ArrayList<ArrayList>(0);

            for (Enumeration e = p.keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                loadEnumId.add(new ArrayList(0));
                loadEnumId.get(loadEnumId.size() - 1).add(key);
                loadEnumId.get(loadEnumId.size() - 1).add(p.getProperty(key));
            }
            idch.setEnumID(loadEnumId);
        } catch (Exception e) {
            idch.setEnumID(new ArrayList<ArrayList>(0));
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();

        }
        return true;
    }

    public ArrayList<ArrayList> CreateRelations() {

        ArrayList<ArrayList> rel = new ArrayList<ArrayList>(0);
        try {
            for (int i = 0; i < this.L_SDocEnums.size(); ++i) {
                RedmineEnum trg = this.FindByName(L_TDocEnums, this.L_SDocEnums.get(i).getName());
                rel.add(new ArrayList(0));
                rel.get(rel.size() - 1).add(this.L_SDocEnums.get(i).getName());
                rel.get(rel.size() - 1).add((trg == null) ? ("new_record") : (trg.getName()));
            }
            for (int i = 0; i < this.L_SPriorEnums.size(); ++i) {
                RedmineEnum trg = this.FindByName(L_TPriorEnums, this.L_SPriorEnums.get(i).getName());
                rel.add(new ArrayList(0));
                rel.get(rel.size() - 1).add(this.L_SPriorEnums.get(i).getName());
                rel.get(rel.size() - 1).add((trg == null) ? ("new_record") : (trg.getName()));
            }
            for (int i = 0; i < this.L_SActEnums.size(); ++i) {
                RedmineEnum trg = this.FindByName(L_TActEnums, this.L_SActEnums.get(i).getName());
                rel.add(new ArrayList(0));
                rel.get(rel.size() - 1).add(this.L_SActEnums.get(i).getName());
                rel.get(rel.size() - 1).add((trg == null) ? ("new_record") : (trg.getName()));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return rel;

    }
}
