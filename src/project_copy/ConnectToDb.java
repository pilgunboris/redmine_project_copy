/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package project_copy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 *
 * @author Pilgun Boris
 */
public class ConnectToDb {

    private PropDB prDB;
    private Properties prop;
    private Connection con = null;

    /*constructor*/
    public ConnectToDb(PropDB prDB, String db_name) throws Exception {
        try {
            this.prDB = prDB;
            this.prop = prDB.getConfProp();

            Class.forName(prop.getProperty("db.driver.class"));

            con = DriverManager.getConnection(prop.getProperty("db.connection.url").toString() + db_name, prop.getProperty("db.username"), prop.getProperty("db.password"));

            System.out.println("Connected");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public Connection getCon() {
        return con;
    }
}
