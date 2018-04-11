package org.yansou.crawl.wd.zhaopin.dao;

import com.alibaba.fastjson.JSONObject;
import org.yansou.crawl.wd.core.BasicSite;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class EntityInsertPipeline<T> {
    private final EntityDao<T> dao;

    public EntityDao<T> getDao() {
        return dao;
    }

    private Class<T> clazz;

    public EntityInsertPipeline(Class<T> clazz) {
        this.clazz = clazz;
        dao = new EntityDao<>( clazz );
    }


    public void process(JSONObject result, BasicSite job) {
        try {

            Closeable finish = (Closeable) result.get( "_finish_" );
            if (null == finish) {
                finish = () -> {
                    System.out.println( "closeable is null." );
                };
            }
            T entity = clazz.newInstance();
            invoke( result, entity );
            printInfo( entity );
            dao.insertOrUpdate( entity, finish );
        } catch (Exception e) {
            throw new IllegalArgumentException( e );
        }
    }

    void printInfo(T entity) {
//        System.out.println( entity );
    }

    public void invoke(JSONObject result, Object entity) {
        HashMap<String, Field> map = new HashMap<String, Field>();
        Arrays.asList( clazz.getFields() ).forEach( field -> {
            map.put( field.getName(), field );
        } );
        result.entrySet().forEach( entry -> {
            String key = entry.getKey();
            Object value = entry.getValue();
            Field field = map.get( key );
            if (null != field) {
                try {
                    if (null != value) {
                        field.set( entity, Objects.toString( value ) );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } );
    }

}
