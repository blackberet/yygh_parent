package com.atguigu.yygh.cmn.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.Map;

public class ExcelListener extends AnalysisEventListener<Student> {


    //每解析一行数据都会调用一次
    @Override
    public void invoke(Student student, AnalysisContext analysisContext) {
        System.out.println(student);
    }

    //解析表头会被调用,标题被封装到Map里面,键是表头的下标,值是标头
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        System.out.println(headMap);
    }

    //没解析一个sheet,都会执行,做收尾工作
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        System.out.println("收尾");
    }
}
