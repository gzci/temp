package org.yansou.common.crawl.util.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;
import javax.swing.*;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.lang3.StringUtils;
import org.yansou.common.crawl.util.Logs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

/**
 * @author 张南
 */
public class JSONObjectStoreMySQLDao implements AutoCloseable {
    final private String tab_name;
    final private String rowkey;
    private String statusInfo;
    private DataSource dataSource;
    private Connection writeConn = null;
    private Connection queryConn = null;
    private QueryRunner qr;
    private volatile boolean closed = false;
    private List<String> fields = new ArrayList<>();
    private String querySql;
    private String queryNotUpdateFieldMapSql;

    public boolean syncStore(JSONObject jObject) {
        if (closed) {
            throw new IllegalStateException( "appDao closed." );
        }
        _save( jObject );
        return true;
    }

    DaoCall getInsCall(JSONObject obj) {
        Objects.requireNonNull( obj );
        DaoCall daoCao = new DaoCall();
        ArrayList<Object> argList = Lists.newArrayList();
        StringBuffer sb = new StringBuffer();
        StringBuffer sbV = new StringBuffer();
        fields.forEach( key -> {
            if (StringUtils.isNotBlank( obj.getString( key ) )) {
                sb.append( key ).append( "," );
                sbV.append( '?' ).append( ',' );
                argList.add( obj.remove( key ) );
            }
        } );
        sb.setLength( sb.length() - 1 );
        sbV.setLength( sbV.length() - 1 );
        daoCao.setSql( "INSERT INTO " + tab_name + " (" + sb + ")VALUES(" + sbV + ")" );
        daoCao.setArgs( argList.toArray() );
        return daoCao;
    }

    DaoCall getUpdate(JSONObject obj, List<String> fieldNames) {
        fields.forEach( field -> {
            if (!obj.containsKey( field )) {
                obj.put( field, "" );
            }
        } );
        DaoCall daoCall = new DaoCall();
        ArrayList<Object> argList = Lists.newArrayList();
        StringBuffer sb = new StringBuffer();
        Object id = obj.remove( "id" );
        if (null == id) {
            throw new IllegalArgumentException( "id is null." );
        }
        sb.append( "UPDATE " + tab_name + " SET " );
        fields.stream().filter( key -> null == fieldNames || (!fieldNames.contains( key )) ).forEach( key -> {
            if (null != obj.getString( key )) {
                sb.append( key ).append( "=?," );
                argList.add( obj.remove( key ) );
            }
        } );
        sb.setLength( sb.length() - 1 );
        sb.append( " WHERE id=?" );
        argList.add( id );
        daoCall.setSql( sb.toString() );
        daoCall.setArgs( argList.toArray() );
        return daoCall;
    }

    /**
     * 创建一个数据库保存对象
     *
     * @param dataSource 数据源
     * @param tab_name   表名
     * @param rowkey     标识唯一性的字段名
     */
    public JSONObjectStoreMySQLDao(DataSource dataSource, String tab_name, String rowkey) {
        this.dataSource = dataSource;
        this.tab_name = tab_name;
        this.rowkey = rowkey;
        initSql();
        qr = new QueryRunner( dataSource );
        readTabField();
        Timer countTimer = new Timer( "UPDATE COUNT", true );
        countTimer.scheduleAtFixedRate( new LmdTask( () -> {
            // --- 后面的参数还没写呢。看到了赶紧写完。
            setStatusInfo( String.format( "UPD:%d/%d INS:%d/%d\n", updCount.get(), allUpdCount.get(), insCount.get(),
                    allInsCount.get() ) );
            updCount.set( 0 );
            insCount.set( 0 );
        } ), 1000, 1000 );
    }

    protected void initSql() {
        querySql = "select id from " + tab_name + " where " + rowkey + "=?";
        queryNotUpdateFieldMapSql = "select * from " + tab_name + " where id=?";
    }

    public Long getIdInRowkey(String rowkey) {
        IDEntry ent = getId( rowkey );
        if (null != ent) {
            return ent.getId();
        }
        return null;
    }

    IDEntry getId(String rowkey) {
        try {
            queryConn = checkConn( queryConn );
            if (null == queryConn) {
                queryConn = dataSource.getConnection();
            }
            IDEntry ent = qr.query( queryConn, querySql, new BeanHandler<>( IDEntry.class ), rowkey );
            if (null != ent) {
                return ent;
            }
            return null;
        } catch (SQLException e) {
            DbUtils.closeQuietly( queryConn );
            queryConn = null;
            throw new IllegalStateException( e );
        }
    }

    List<String> getNotUpdateFieldName(long id) {
        queryConn = checkConn( queryConn );
        try {
            if (null == queryConn) {
                queryConn = dataSource.getConnection();
            }
            Map<String, Object> ent = qr.query( queryConn, queryNotUpdateFieldMapSql, new MapHandler(), id );
            ent.entrySet().stream().filter( e -> {
                return e.getValue() != null && StringUtils.isNotBlank( e.getValue().toString() );
            } );
            return null;
        } catch (SQLException e) {
            DbUtils.closeQuietly( queryConn );
            queryConn = null;
            throw new IllegalStateException( e );
        }
    }

    DaoCall getCall(JSONObject obj) {
        queryConn = checkConn( queryConn );
        if (null == queryConn) {
            try {
                queryConn = dataSource.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    void readTabField() {
        fields.clear();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery( "SELECT * FROM " + tab_name + " WHERE 1=2" );
            ResultSetMetaData rsmd = rs.getMetaData();
            int cols = rsmd.getColumnCount();
            for (int i = 1; i <= cols; i++) {
                String columnName = rsmd.getColumnLabel( i );
                String type = rsmd.getColumnTypeName( i ).toUpperCase();
                if (type.contains( "CHAR" ) || type.contains( "FLOAT" ) || type.contains( "INT" ) || type.contains( "TEXT" )
                        || type.contains( "TIME" )) {
                    fields.add( columnName );
                }
            }
            Logs.info( "Fields:" + fields );
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly( conn, stmt, rs );
        }
    }

    public static class IDEntry {
        private Long id;
        private long status;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getStatus() {
            return status;
        }

        public void setStatus(Long status) {
            this.status = status;
        }

    }

    Object[] argsConversion(Object[] args) {
        Object[] res = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof JSON) {
                res[i] = args[i].toString();
            } else {
                res[i] = args[i];
            }
        }
        return res;
    }

    void _save(JSONObject json) {
        DaoCall daoCall = null;
        try {
            writeConn = checkConn( writeConn );
            if (null == writeConn) {
                writeConn = dataSource.getConnection();
            }
            IDEntry ent = getId( json.getString( rowkey ) );
            if (null != ent && ent.getStatus() != 0) {
                // 人工改动过的记录,不修改。
                Logs.info( "skip save:{}" + ent.getId() );
                return;
            }
            if (null == ent) {
                daoCall = getInsCall( json );
                System.out.println("not found but insert:"+daoCall.getSql());
            } else {
                long id = ent.getId();
                json.put( "id", id );
                List<String> fieldNames = getNotUpdateFieldName( id );
                daoCall = getUpdate( json, fieldNames );
                System.out.println("found! and update:"+daoCall.getSql());
            }
            qr.update( writeConn, daoCall.getSql(), argsConversion( daoCall.getArgs() ) );
            if (StringUtils.startsWithIgnoreCase( daoCall.getSql(), "UPDATE" )) {
                updCount.incrementAndGet();
                allUpdCount.incrementAndGet();
            } else if (StringUtils.startsWithIgnoreCase( daoCall.getSql(), "INSERT" )) {
                insCount.incrementAndGet();
                allInsCount.incrementAndGet();
            } else {
                errCount.incrementAndGet();
            }
        } catch (Exception e) {
            DbUtils.closeQuietly( writeConn );
            writeConn = null;
            Logs.error( e.getMessage() + "---{} {}", daoCall, e );
        }
    }

    AtomicInteger allUpdCount = new AtomicInteger( 0 );
    AtomicInteger allInsCount = new AtomicInteger( 0 );
    AtomicInteger updCount = new AtomicInteger( 0 );
    AtomicInteger insCount = new AtomicInteger( 0 );
    AtomicInteger errCount = new AtomicInteger( 0 );

    private class DaoCall {
        private String sql;
        private Object[] args;

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }

        public Object[] getArgs() {
            return args;
        }

        public void setArgs(Object[] args) {
            this.args = args;

        }

        @SuppressWarnings("unused")
        public boolean isOK() {
            String whs = sql.replaceAll( "\\?", "" );
            System.out.println( whs );
            return whs.length() == args.length;
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    private static class LmdTask extends TimerTask {
        public LmdTask(Runnable runnable) {
            this.runable = runnable;
        }

        private final Runnable runable;

        @Override
        public void run() {
            runable.run();
        }
    }

    @Override
    public void close() {
        closed = true;
        dataSource = null;
        DbUtils.closeQuietly( writeConn );
        DbUtils.closeQuietly( queryConn );
    }

    private IdentityHashMap<Connection, Long> timeOutMap = new IdentityHashMap<>();

    private Connection checkConn(Connection conn) {
        if (Objects.isNull( conn )) {
            return null;
        }
        Long timeOut = timeOutMap.get( conn );
        if (Objects.isNull( timeOut )) {
            timeOut = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis( 300 );
            timeOutMap.put( conn, timeOut );
        } else if (timeOut < System.currentTimeMillis()) {
            timeOutMap.remove( conn );
            DbUtils.closeQuietly( conn );
            return null;
        }
        return conn;
    }

    public String getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(String statusInfo) {
        this.statusInfo = statusInfo;
    }
}
