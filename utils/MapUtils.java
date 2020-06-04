/*
 * Copyright 2017 @ursful.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yidao.court.prelitigation.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class MapUtils {
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> converter(Map<String, Object> map){

        Map<String, Object> temp = new HashMap<String, Object>();
        Set<String> keys = map.keySet();
        for(String key : keys){
            String ks [] = key.split("[.]");
            Map<String, Object> nextMap = temp;
            for(int i = 0; i < ks.length; i++){
                Map<String, Object> current = nextMap;
                if(i == ks.length -1){
                    if(map.get(key) instanceof Map){
                        Map<String, Object> tmp = (Map<String, Object>)map.get(key);
                        nextMap.put(ks[i], converter(tmp));
                    }else {
                        nextMap.put(ks[i], map.get(key));
                    }
                }else {
                    Object obj = nextMap.get(ks[i]);
                    if(obj != null && !(obj instanceof Map)){
                        continue;
                    }
                    nextMap = (Map<String, Object>)obj;
                    if(nextMap == null){
                        nextMap = new HashMap<String, Object>();
                    }
                    current.put(ks[i], nextMap);
                }
            }
        }
        return temp;
    }

    public static void main(String[] args) throws Exception{

        Properties properties = new Properties();
        File file = new File("/Users/ynice/Desktop/weitu/weitu-site/src/main/resources/config.properties");
        properties.load(new FileInputStream(file));
        //Map<String, Object> map = new HashMap<>();

        //System.out.println(properties);

        Map<String, Object> map = converter(MapUtils.newHashMap("file.upload.max.file.size", 1,"file.upload.max.size", 2, "a.b.c",4));
        System.out.println(map);

        map.clear();
        System.out.println(properties.size());
        for(Object key : properties.keySet()){
            map.put(key.toString(), properties.get(key));
        }
         map = converter(map);


        System.out.println(map);
    }

    public static Map<String, String> reverse(Map<String, String> map){
        Map<String, String> temp = new HashMap<String,String>();
        if(map != null){
            Set<String> keys = map.keySet();
            for(String key : keys){
                temp.put(map.get(key), key);
            }
        }
        return temp;
    }

    public static  Map<String, Object> newHashMap(Object ... parameters){
        Map<String, Object> map = new HashMap<String, Object>();
        if(parameters == null){
            return map;
        }
        for(int i = 0; i < parameters.length; i=i+2){
            Object key = parameters[i];
            if(key == null){
                continue;
            }
            Object value = null;
            if(i + 1 < parameters.length){
                value = parameters[i+1];
            }
            map.put(key.toString(), value);
        }
        return map;
    }



}
