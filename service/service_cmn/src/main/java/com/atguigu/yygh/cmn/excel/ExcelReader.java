package com.atguigu.yygh.cmn.excel;

import com.alibaba.excel.EasyExcel;

public class ExcelReader {
    public static void main(String[] args) {
        String fileName = "D:\\Desktop\\student.xlsx";
        EasyExcel.read(fileName,Student.class,new ExcelListener()).sheet().doRead();
    }
}
