package org.yansou.crawl.wd.zhaopin.dao;

import com.google.common.collect.Lists;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;
import org.yansou.crawl.wd.core.DBField;
import org.yansou.crawl.wd.core.DBNotNull;
import org.yansou.crawl.wd.core.DBRowKey;
import org.yansou.crawl.wd.core.DBTable;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicReference;


/**
 * 描述信息：
 *
 * @author: liuqingchao - liuqingchao@pyc.com.cn
 * @data: 2015年4月7日
 */
public class EntityDao<E> extends AbsDao {

    private static Logger logger = Logger.getLogger( EntityDao.class );
    private Thread th;
    private static boolean isMapper = true;

    private void init() {
        if (th == null) {
            th = new Thread( () -> {
                while (true) {
                    MyEntry entry = null;
                    try {
                        entry = queue.take();
                    } catch (InterruptedException e) {
                        continue;
                    }
                    ins( entry.getT(), entry.getCallback() );
                }
            } );
            th.setDaemon( true );
            th.setName( "[EntityDaoThread]" );
            th.start();
            // 增加关机钩子，如果需要关机时发现有没入库的则执行入库。
            Runtime.getRuntime().addShutdownHook( new Thread( () -> {
                while (true) {
                    MyEntry entry = null;
                    entry = queue.poll();
                    if (null == entry) {
                        return;
                    }
                    System.out.println( "关机前入库。。。" );
                    ins( entry.getT(), entry.getCallback() );
                }
            } ) );
        }
    }

    private Class<?> clazz;

    public void update(E e) {
        update( e, emptyCallback );
    }

    QueryRunner qr = new QueryRunner();
    Object updateLock = new Object();

    public void update(E t, Closeable callback) {
        if (null == updateConn) {
            synchronized (updateLock) {
                if (null == updateConn) {
                    updateConn = C3p0Pool.install().getConnection();
                }
            }
        }
        updateInObject( t, updateConn );
        try {
            callback.close();
        } catch (IOException e) {
        }
    }

    private void updateInObject(Object obj, Connection conn) {
        List<Object> params = Lists.newArrayList();
        AtomicReference<Object> idVal = new AtomicReference<>();
        AtomicReference<String> idName = new AtomicReference<>();
        AtomicReference<Boolean> updateOn = new AtomicReference<>( false );
        DBTable table = clazz.getAnnotation( DBTable.class );
        StringBuilder sb = new StringBuilder();
        sb.append( "UPDATE" );
        sb.append( ' ' );
        sb.append( table.value() );
        sb.append( ' ' );
        sb.append( "SET" );
        sb.append( ' ' );
        table.value();
        Arrays.asList( clazz.getFields() ).forEach( field -> {
            try {
                Object val = field.get( obj );
                if (null != val) {
                    if (val instanceof Number && 0 == ((Number) val).intValue()) {
                        return;
                    }
                    DBField anno = field.getAnnotation( DBField.class );
                    if (null != field.getAnnotation( DBRowKey.class )) {
                        idVal.set( val );
                        idName.set( anno.name() );
                    } else if (null != anno) {
                        sb.append( anno.name() );
                        sb.append( '=' );
                        sb.append( '?' );
                        sb.append( ',' );
                        params.add( val );
                        updateOn.set( true );
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } );
        if (updateOn.get() && null != idName.get() && null != idVal.get()) {
            sb.setLength( sb.length() - 1 );
            sb.append( ' ' );
            sb.append( "WHERE" );
            sb.append( ' ' );
            sb.append( idName.get() );
            sb.append( '=' );
            sb.append( '?' );
            params.add( idVal.get() );
            try {
                System.out.println( "update:" + qr.update( conn, sb.toString(), params.toArray() ) );
                System.out.println( sb.toString() );
                System.out.println( params );
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println( "不能更新:" + obj );
        }
    }

    public void ins(E e) {
        ins( e, emptyCallback );
    }

    Connection updateConn;

    public void ins(E entity, Closeable callback) {
        PreparedStatement ps = null;
        if (!isStore0( entity )) {
            System.out.println( "不满足入库条件:" + entity );
            return;
        }
        String sql = null;
        try {
            if (conn == null) {
                conn = C3p0Pool.install().getConnection();
            }
            if (!isMapper) {
                sql = EntityToString.getInsert( entity, clazz );
                ps = conn.prepareStatement( sql );
            } else {
                ps = EntityToString.getPsInsert( entity, clazz, conn );
            }
            int num = ps.executeUpdate();
            System.out.println( "insert:" + num );
            callback.close();
        } catch (Exception e) {
            if (e.getMessage().contains( "Duplicate entry" )) {
                System.out.println( "有唯一约束:" + e.getMessage() );
            } else {
                System.err.println( "ERRSQL:" + sql );
                e.printStackTrace();

                logger.error( "数据库链接出错！！！", e );
            }
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // public static void mapping(Paper paper) {
    // try {
    // ps.setString(1, paper.getTitle());
    // ps.setString(2, paper.getAuthors());
    // ps.setString(3, paper.getWorkUnit());
    // ps.setString(4, paper.getKeywords());
    // ps.setString(5, paper.getAbstracts());
    // ps.setString(6, paper.getDoi());
    // ps.setString(7, paper.getCited() + "");
    // ps.setString(8, paper.getSelfCited() + "");
    // ps.setString(9, paper.getDownloadUrl());
    // ps.setString(10, paper.getDownloadUrlBak());
    // ps.setString(11, paper.getPublishTime());
    // ps.setString(12, paper.getReferences());
    // ps.setString(13, paper.getArticleName());
    // ps.setString(14, paper.getLocalAddress());
    // ps.setString(15, paper.getSource());
    // ps.setString(16, paper.getEnterUrl());
    // ps.setString(17, paper.getFetchUrl());
    // ps.setString(18, paper.getSubject());
    // ps.setString(19, paper.getVolume());
    // ps.setString(20, paper.getNoOrIssue());
    // ps.setString(21, paper.getPages());
    // ps.setString(22, paper.getContributeOffer());
    // ps.setString(23, paper.getConferenceAddress());
    // ps.setString(24, paper.getPublishing());
    // ps.setString(25, paper.getIssn());
    // } catch (SQLException e) {
    // e.printStackTrace();
    // }
    // }

    private BlockingQueue<MyEntry> queue = new LinkedBlockingDeque<MyEntry>( 10 );
    static Closeable emptyCallback = () -> {
    };

    public synchronized void store(E t) {
        if (t != null) {
            try {
                queue.put( new MyEntry( t, emptyCallback ) );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void store(E t, Closeable callback) {
        if (t != null) {
            try {
                queue.put( new MyEntry( t, callback ) );
            } catch (InterruptedException e) {
                throw new IllegalStateException( e );
            }
        }
    }

    public EntityDao(Class<E> clazz) {

        this.clazz = clazz;

        synchronized (EntityDao.class) {
            // 判断该表是否存在,不存在就创建
            if (!isExistTable( this.clazz )) {
                createTable( this.clazz );
            }
            System.out.println( "create paper success!" );
            init();
        }
    }

    Field rowKeyField;
    boolean isRowKey = true;
    Field[] notNullfields;

    public boolean isUpdate(E obj) {
        checkRowKey();
        if (null == isUpdateSQL) {
            isUpdateSQL = EntityToString.getSelect( obj, clazz, rowKeyField );
            System.out.println( isUpdateSQL );
        }
        try {
            Object val = rowKeyField.get( obj );
            return qr.query( conn, isUpdateSQL, (rs) -> rs.next(), val );
        } catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    String isUpdateSQL;

    private void checkRowKey() {
        // 检查数据库中是否有对应字段
        if (isRowKey && null == rowKeyField) {
            ArrayList<Field> list = Lists.newArrayList();
            Arrays.asList( clazz.getFields() ).stream().filter( f -> null != f.getAnnotation( DBRowKey.class ) )
                    .forEach( list::add );
            if (list.size() == 1) {
                rowKeyField = list.get( 0 );
            } else if (list.size() > 1) {
                throw new IllegalStateException( "rowKey只能有一个字段具有" );
            } else {
                isRowKey = false;
            }
        }
    }

    public void insertOrUpdate(E e, Closeable callback) {
        if (isUpdate( e )) {
            update( e, callback );
        } else {
            ins( e, callback );
        }
    }

    public void insertOrUpdate(E e) {
        insertOrUpdate( e, emptyCallback );
    }

    /**
     * 判断是否入库
     *
     * @param value
     * @return
     */
    public boolean isInsert(String value) {
        checkRowKey();
        if (null != rowKeyField) {
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                String sql = EntityToString.getSelect( value, clazz, rowKeyField );
                if (null == conn) {
                    conn = C3p0Pool.install().getConnection();
                }
                ps = conn.prepareStatement( sql );
                ps.setString( 1, value.toString() );
                rs = ps.executeQuery();
                if (rs.next()) {
                    return false;
                }
            } catch (IllegalArgumentException | SQLException e) {
                DbUtils.closeQuietly( conn );
                conn = null;
                return false;
            } finally {
                if (null != rs) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;
    }

    boolean isStore0(E t) {
        // 检查必要字段是否都齐全
        if (null == notNullfields) {
            ArrayList<Field> list = Lists.newArrayList();
            Arrays.asList( clazz.getFields() ).stream().filter( f -> null != f.getAnnotation( DBNotNull.class ) )
                    .forEach( list::add );
            notNullfields = list.toArray( new Field[list.size()] );
        }
        for (int i = 0; i < notNullfields.length; i++) {
            try {
                if (null == notNullfields[i].get( t )) {
                    return false;
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                return false;
            }
        }

        return true;
    }

    private class MyEntry {
        public MyEntry(E t, Closeable callback) {
            this.t = t;
            this.callback = callback;
        }

        private final E t;
        private final Closeable callback;

        public E getT() {
            return t;
        }

        public Closeable getCallback() {
            return callback;
        }

    }
}
