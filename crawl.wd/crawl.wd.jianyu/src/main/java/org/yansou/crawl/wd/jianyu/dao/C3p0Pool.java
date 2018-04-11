package org.yansou.crawl.wd.jianyu.dao;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 描述信息：C3P0实现连接池
 *
 * @author: liuqingchao - liuqingchao@pyc.com.cn
 * @data: 2015年4月8日
 */
public class C3p0Pool {

    private static ComboPooledDataSource cpds = null;
    private static C3p0Pool dbManager;

    private C3p0Pool() {
        cpds = new ComboPooledDataSource( true );
        /**
         * 参数配置
         */
        cpds.setDataSourceName( "hngp" );
        cpds.setJdbcUrl( "jdbc:mysql://biaoshuking.mysql.rds.aliyuncs.com:3306/hngp?useUnicode=true&characterEncoding=utf-8" );
        try {
            cpds.setDriverClass( "com.mysql.jdbc.Driver" );
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        cpds.setUser( "hngp" );
        cpds.setPassword( "hngp123" );
        cpds.setMinPoolSize( 2 );
        cpds.setMaxPoolSize( 30 );
        cpds.setAcquireIncrement( 10 );
        cpds.setInitialPoolSize( 1 );
        cpds.setMaxIdleTime( 0 );
    }

    public static C3p0Pool install() {
        if (dbManager == null) {
            synchronized (C3p0Pool.class) {
                if (dbManager == null) {
                    dbManager = new C3p0Pool();
                }
            }
        }
        return dbManager;
    }

    public synchronized Connection getConnection() {
        System.out.println( "c3p0 connected" );
        try {
            return cpds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized void returnConnection(Connection connection) {
    }
}