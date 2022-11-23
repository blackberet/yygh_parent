package com.atguigu.yygh.hosp.result;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpRequestHelper {
    public static Map<String,String> switchMap(Map<String, String[]> map){
        HashMap<String, String> hashMap = new HashMap<>();
        Set<Map.Entry<String, String[]>> entries = map.entrySet();
        for (Map.Entry<String, String[]> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue()[0];
            hashMap.put(key,value);
        }
        return hashMap;
    }

}
