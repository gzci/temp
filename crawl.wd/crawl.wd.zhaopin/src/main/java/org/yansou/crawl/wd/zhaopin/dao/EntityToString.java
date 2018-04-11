package org.yansou.crawl.wd.zhaopin.dao;

import org.yansou.crawl.wd.core.DBAutoIncrement;
import org.yansou.crawl.wd.core.DBField;
import org.yansou.crawl.wd.core.DBTable;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 描述信息：
 *
 * @author: liuqingchao - liuqingchao@pyc.com.cn
 * @data: 2015年4月8日
 */
public class EntityToString {
    /**
     * 描述信息：创建表
     *
     * @data: 2015年4月8日
     */
    public static String getCreateTable(Class<?> clazz) {
        String result = "create table ";
        DBTable table = clazz.getAnnotation( DBTable.class );
        result = result + table.value() + "(";
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            DBField dbField = field.getAnnotation( DBField.class );
            result = result + dbField.name() + " " + dbField.type() + ",";
        }
        result = result.substring( 0, result.length() - 1 )
                + " )engine=innodb default charset=utf8";
        return result;
    }

    public static String getSelect(Object obj, Class<?> clazz, Field field) {
        String table = clazz.getAnnotation( DBTable.class ).value();
        DBField rowKeyDbField = field.getAnnotation( DBField.class );
        return "select " + rowKeyDbField.name() + " from " + table + " where "
                + rowKeyDbField.name() + "=?";
    }

    /**
     * 描述信息：创建插入语句，直接带值的插入语句
     *
     * @data: 2015年4月8日
     */
    public static String getInsert(Object obj, Class<?> clazz)
            throws IllegalArgumentException, IllegalAccessException {
        String result = "insert into ";
        String values = "values(";
        String table = clazz.getAnnotation( DBTable.class ).value();
        result = result + table + "(";
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            if ("id".equals( field.getName() )) {
                continue;
            }
            if (null == field.get( obj )) {
                continue;
            }
            DBField dbField = field.getAnnotation( DBField.class );
            result = result + dbField.name() + ",";
            if (dbField.type().startsWith( "varchar" )
                    || dbField.type().startsWith( "text" )) {
                values = values + "'" + field.get( obj ) + "',";
            } else
                values = values + field.get( obj ) + ",";
        }
        result = result.substring( 0, result.length() - 1 ) + ")";
        values = values.substring( 0, values.length() - 1 ) + ")";
        return result + " " + values;
    }

    public interface QRUpdate {
        public int update(Connection conn, String sql, Object... params)
                throws SQLException;
    }

    /**
     * 描述信息：直接返回包装好的preparedStatment
     *
     * @data: 2015年4月14日
     */
    public static PreparedStatement getPsInsert(Object obj, Class<?> clazz,
                                                Connection conn) throws IllegalArgumentException,
            IllegalAccessException, SQLException {
        PreparedStatement ps = null;
        String result = "insert into ";
        String values = "values(";
        String table = clazz.getAnnotation( DBTable.class ).value();
        result = result + table + "(";
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            if (null != field.getAnnotation( DBAutoIncrement.class )) {
                continue;
            }
            if (null == field.get( obj )) {
                continue;
            }
            DBField dbField = field.getAnnotation( DBField.class );
            result = result + dbField.name() + ",";
            values = values + "?" + ",";
            // if (dbField.type().startsWith("varchar")
            // || dbField.type().startsWith("text")) {
            // values = values + "'" + field.get(obj) + "',";
            // } else
            // values = values + field.get(obj) + ",";
        }
        result = result.substring( 0, result.length() - 1 ) + ")";
        values = values.substring( 0, values.length() - 1 ) + ")";
        ps = conn.prepareStatement( result + " " + values );
        // System.out.println("存储sql:" + result + " " + values);
        int i = 1;
        for (Field field : fields) {
            if (null != field.getAnnotation( DBAutoIncrement.class )) {
                continue;
            }
            if (null == field.get( obj )) {
                continue;
            }
            ps.setString( i, field.get( obj ).toString() );
            i++;
        }
        return ps;
    }

}