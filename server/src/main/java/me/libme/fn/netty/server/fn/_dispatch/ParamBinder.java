package me.libme.fn.netty.server.fn._dispatch;

import me.libme.fn.netty.server.HttpRequest;
import me.libme.kernel._c.bean.ObjectBinder;
import me.libme.kernel._c.bean.SimpleObjectBinder;
import me.libme.kernel._c.util.DateConverter;
import me.libme.kernel._c.util.JClassUtils;
import me.libme.kernel._c.util.JStringUtils;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by J on 2018/1/27.
 */
public class ParamBinder<O> implements ObjectBinder<HttpRequest,O> {

    private final Class<O> _clazz;

    public ParamBinder(Class<O> clazz) {
        this._clazz = clazz;
    }

    private SimpleObjectBinder<O> simpleObjectBinder=new SimpleObjectBinder<O>() {};

    @Override
    public O createObject(HttpRequest content) {

        O object=simpleObjectBinder.createObject(_clazz);
        Class<?> clazz=_clazz;

        while(clazz!=null){
            Field[] fields=clazz.getDeclaredFields();
            if(fields.length>0){
                for(Field field:fields){
                    try {
                        set(content,object,field);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(field.getName());
                    }
                }
            }
            clazz=clazz.getSuperclass();
        }
        return object;
    }



    private void set(HttpRequest httpRequest,O object,Field field) throws Exception {
        field.setAccessible(true);
        Class clazz=field.getDeclaringClass();
        if(JClassUtils.isSimpleType(clazz)){
            String fieldName=field.getName();
            String val=httpRequest.getParam(fieldName);

            if(JStringUtils.isNullOrEmpty(val)){
                return;
            }

            if(String.class.isAssignableFrom(clazz)){
                field.set(object,httpRequest.getParam(fieldName));
            }
            else if (Integer.class.isAssignableFrom(clazz)
                    ||int.class.isAssignableFrom(clazz)){
                field.setInt(object,Integer.valueOf(val));
            }
            else if (Long.class.isAssignableFrom(clazz)
                    ||long.class.isAssignableFrom(clazz)){
                field.setLong(object,Long.valueOf(val));
            }
            else if (Double.class.isAssignableFrom(clazz)
                    ||double.class.isAssignableFrom(clazz)){
                field.setDouble(object,Double.valueOf(val));
            }
            else if (Float.class.isAssignableFrom(clazz)
                    ||float.class.isAssignableFrom(clazz)){
                field.setFloat(object,Float.valueOf(val));
            }
            else if (Byte.class.isAssignableFrom(clazz)
                    ||byte.class.isAssignableFrom(clazz)){
                field.setByte(object,Byte.valueOf(val));
            }
            else if (Boolean.class.isAssignableFrom(clazz)
                    ||boolean.class.isAssignableFrom(clazz)){
                field.setBoolean(object,Boolean.valueOf(val));
            }
            else if (Timestamp.class==clazz){
                field.set(object,new Timestamp(DateConverter.DATE_CONVERTER.convert(val).getTime()));
            }
            else if (Date.class.isAssignableFrom(clazz)){
                field.set(object,DateConverter.DATE_CONVERTER.convert(val).getTime());
            }

        }else if(Map.class.isAssignableFrom(clazz)){
            map(httpRequest, object, field);
        }else if(List.class.isAssignableFrom(clazz)){
            list(httpRequest, object, field);
        }

    }


    private void list(HttpRequest httpRequest,O object,Field field){



    }

    private void map(HttpRequest httpRequest,O object,Field field){



    }


}
