package org.yansou.crawl.wd.zhaopin.dao;


import org.yansou.crawl.wd.core.DBTable;

import java.sql.*;

/**
 * 描述信息：
 *
 * @author: liuqingchao - liuqingchao@pyc.com.cn
 * @data: 2015年4月8日
 */
public abstract class AbsDao {

    public Connection conn = C3p0Pool.install().getConnection();


    public boolean isExistTable(Class<?> clazz) {

        String tableName = "";
        DatabaseMetaData meta = null;
        ResultSet rs = null;
        try {
            String name = clazz.getAnnotation( DBTable.class ).value();

            tableName = name;

            if (conn == null) {
                conn = C3p0Pool.install().getConnection();
            }
            meta = conn.getMetaData();
            rs = meta.getTables( null, null, tableName, null );
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public void createTable(Class<?> clazz) {
        PreparedStatement ps = null;
        if (conn == null) {
            conn = C3p0Pool.install().getConnection();
        }
        try {
            String table = clazz.getAnnotation( DBTable.class ).value();
            String sql = EntityToString.getCreateTable( clazz );
            System.out.println( sql );
            ps = conn.prepareStatement( sql );

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
