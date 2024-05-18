package com.easyjava.Utils;

import sun.awt.image.InputStreamImageSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @FileName PropertiesUtils
 * @Description 获取资源
 * @Author fahrtwind
 * @date 2024-04-28
 **/


public class PropertiesUtils {
    private static Properties props=new Properties();
    private static Map<String,String> PROPER_MAP = new ConcurrentHashMap();

    static {
        InputStream is = null;
        try {
            is = PropertiesUtils.class.getClassLoader().getResourceAsStream("application.yml");
            props.load(new InputStreamReader(is,"utf-8"));

            Iterator<Object> iterator = props.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                PROPER_MAP.put(key,props.getProperty(key));
            }
        } catch (Exception e){

        } finally {
            if (is != null){
                try{
                    is.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getString(String key){
        return PROPER_MAP.get(key);
    }

}
