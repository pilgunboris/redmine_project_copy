/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db_controls;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;
import java.io.Reader;
import java.util.logging.Logger;

/**
 *
 * @author root
 */
public class AppSqlConfig {

    private static final String resource = "SqlMapConfig.xml";
    private static AppSqlConfig instance;
    private SqlMapClient sqlMapper;

    private AppSqlConfig() {
        try {

            Reader reader = Resources.getResourceAsReader(resource);
            sqlMapper = SqlMapClientBuilder.buildSqlMapClient(reader);
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Error initializing MyAppSqlConfig class. Cause: " + e);
        }
    }

    public static AppSqlConfig getInstance() {
        if (instance == null) {
            instance = new AppSqlConfig();
        }
        return instance;
    }

    public SqlMapClient getSqlMapper() {
        return sqlMapper;
    }
}
