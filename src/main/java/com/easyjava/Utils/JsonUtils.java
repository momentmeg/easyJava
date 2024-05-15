package com.easyjava.Utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;


/**
 * @FileName JsonUtils
 * @Description
 * @Author fahrtwind
 * @date 2024-05-01
 **/


public class JsonUtils {
    public static String convertObj2Json(Object obj){
        if(null == obj){
            return null;
        }
        return JSON.toJSONString(obj, SerializerFeature.DisableCircularReferenceDetect);
    }

}
