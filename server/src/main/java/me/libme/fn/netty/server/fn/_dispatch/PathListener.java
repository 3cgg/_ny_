package me.libme.fn.netty.server.fn._dispatch;

import me.libme.fn.netty.server.HttpRequest;
import me.libme.kernel._c.cache.JCacheService;
import me.libme.kernel._c.cache.JMapCacheService;
import me.libme.kernel._c.util.DateConverter;
import me.libme.kernel._c.util.JClassUtils;
import me.libme.kernel._c.util.JReflectionUtils;
import me.libme.kernel._c.util.JStringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 所有的实现类只能有一个公共方法
 * Created by J on 2018/1/27.
 */
public interface PathListener {

    default Object listen(HttpRequest httpRequest) throws Exception{

        Class clazz=this.getClass();
        Method method=MethodCache.get().get(clazz);
        if(method==null){
            //then find public method
            Method[] methods=clazz.getDeclaredMethods();
            for(Method md:methods){
                if(Modifier.isPublic(md.getModifiers())){
                    method=md;
                    MethodCache.get().put(clazz,md);
                }
            }
        }

        Object[] objects=new Object[method.getParameterCount()];
        int i=0;
        for(Parameter parameter:method.getParameters()){
            Class type=parameter.getType();
            Object object=null;
            if(HttpRequest.class==type){
                object=httpRequest;
            }else {

                if(JClassUtils.isSimpleType(type)){

                    String name=parameter.getName();
                    if(String.class==type){
                        object=httpRequest.getParam(name);
                    }
                    else if (Integer.class==type){
                        object=JStringUtils.isNullOrEmpty(httpRequest.getParam(name))?null
                                :Integer.valueOf(httpRequest.getParam(name));
                    }else if (int.class==type){
                        object=JStringUtils.isNullOrEmpty(httpRequest.getParam(name))?0
                                :Integer.valueOf(httpRequest.getParam(name));
                    }
                    else if (Long.class==type){
                        object=JStringUtils.isNullOrEmpty(httpRequest.getParam(name))?null
                                :Long.valueOf(httpRequest.getParam(name));
                    }else if (long.class==type){
                        object=JStringUtils.isNullOrEmpty(httpRequest.getParam(name))?0l
                                :Long.valueOf(httpRequest.getParam(name));
                    }else if (Short.class==type){
                        object=JStringUtils.isNullOrEmpty(httpRequest.getParam(name))?null
                                :Short.valueOf(httpRequest.getParam(name));
                    }else if (short.class==type){
                        object=JStringUtils.isNullOrEmpty(httpRequest.getParam(name))?(short)0
                                :Short.valueOf(httpRequest.getParam(name));
                    }else if (Float.class==type){
                        object=JStringUtils.isNullOrEmpty(httpRequest.getParam(name))?null
                                :Float.valueOf(httpRequest.getParam(name));
                    }else if (float.class==type){
                        object=JStringUtils.isNullOrEmpty(httpRequest.getParam(name))?0f
                                :Float.valueOf(httpRequest.getParam(name));
                    }else if (Double.class==type){
                        object=JStringUtils.isNullOrEmpty(httpRequest.getParam(name))?null
                                :Double.valueOf(httpRequest.getParam(name));
                    }else if (double.class==type){
                        object=JStringUtils.isNullOrEmpty(httpRequest.getParam(name))?0d
                                :Double.valueOf(httpRequest.getParam(name));
                    }else if (Boolean.class==type){
                        object=JStringUtils.isNullOrEmpty(httpRequest.getParam(name))?null
                                :Boolean.valueOf(httpRequest.getParam(name));
                    }else if (boolean.class==type){
                        object=JStringUtils.isNullOrEmpty(httpRequest.getParam(name))?true
                                :Boolean.valueOf(httpRequest.getParam(name));
                    }else if (Timestamp.class==type){
                        object=JStringUtils.isNullOrEmpty(httpRequest.getParam(name))?null
                                :new Timestamp(DateConverter.DATE_CONVERTER.convert(httpRequest.getParam(name)).getTime());
                    }else if (Date.class==type){
                        object=JStringUtils.isNullOrEmpty(httpRequest.getParam(name))?null
                                :DateConverter.DATE_CONVERTER.convert(httpRequest.getParam(name));
                    }

                }else{
                    ParamBinder paramBinder=ParamBinderCache.get().get(type);
                    if(paramBinder==null){
                        paramBinder=new ParamBinder(type);
                        ParamBinderCache.get().put(type,paramBinder);
                    }
                    object=paramBinder.createObject(httpRequest);
                }
            }
            objects[i++]=object;
        }

        return  JReflectionUtils.invoke(this,method,objects);
    }

    class MethodCache{

        JCacheService<Class<?>,Method> cache=new JMapCacheService<>();

        private static MethodCache instance=new MethodCache();

        static MethodCache get(){
            return instance;
        }

        Method get(Class<?> clazz){
            return cache.get(clazz);
        }

        void put(Class<?> clazz,Method method){
            cache.put(clazz,method);
        }


    }

    class ParamBinderCache{

        JCacheService<Class,ParamBinder> cache=new JMapCacheService<>();

        private static ParamBinderCache instance=new ParamBinderCache();

        static ParamBinderCache get(){
            return instance;
        }

        <T> ParamBinder<T> get(Class<T> clazz){
            return cache.get(clazz);
        }

        void put(Class<?> clazz,ParamBinder paramBinder){
            cache.put(clazz,paramBinder);
        }


    }


}
