package com.atguigu.yygh.common.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.text.SimpleDateFormat;
import java.util.*;

public class ConvertObject {

    public static Page listToPage(List list, int currentPage, int pageSize){
        List pageList = new ArrayList<>();
        int curIdx = currentPage> 1 ? (currentPage- 1) * pageSize : 0;
        for (int i = 0; i < pageSize && curIdx + i < list.size(); i++) {
            pageList.add(list.get(curIdx + i));
        }
        Page page = new Page<>(currentPage, pageSize);
        page.setRecords(pageList);
        page.setTotal(list.size());
        return page;
    }


    public static Map<String, Object> sortMapByKey(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, Object> sortMap = new TreeMap<String, Object>(new Comparator<String>() {
            @Override
            public int compare(String o1,String o2) {
                return ((String)o1).compareTo((String) o2);
            }
        });
        sortMap.putAll(map);
        return sortMap;
    }

    public static Map<String, Object> sortMapByKeyDate(Map<String, Object> map,String formatType,Date date) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, Object> sortMap = new TreeMap<String, Object>(new Comparator<String>() {
            @Override
            public int compare(String o1,String o2) {
                new SimpleDateFormat(formatType).format(date);
                return ((String)o1).compareTo((String) o2);
            }
        });
        sortMap.putAll(map);
        return sortMap;
    }


}
