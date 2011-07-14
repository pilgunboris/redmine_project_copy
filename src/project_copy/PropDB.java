/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package project_copy;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 *
 * @author Pilgun Boris
 */

/**
 *by pattern Singleton
 */
public class PropDB {

    private Properties confProp;
    private static PropDB instance = null;

    private PropDB() {
        try{
            confProp = new Properties();
            FileInputStream fis = new FileInputStream(new File("config.properties"));
            confProp.load(fis);
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static PropDB getInstance() throws Exception{
        if(instance == null){
            instance = new PropDB();
        }
        return instance;
    }

    public Properties getConfProp() {
        return confProp;
    }


}