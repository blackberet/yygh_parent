package com.atguigu.yygh.cmn.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;

public class ExcelReader2 {
    public static void main(String[] args) {
        String fileName = "D:\\Desktop\\student3.xlsx";
        ExcelReader excelReader = EasyExcel.read(fileName).build();
        ReadSheet readSheet1 = EasyExcel.readSheet(0).head(Student.class).registerReadListener(new ExcelListener()).build();
        ReadSheet readSheet2 = EasyExcel.readSheet(1).head(Teacher.class).registerReadListener(new ExcelListener2()).build();
        excelReader.read(readSheet1,readSheet2);
        excelReader.finish();
    }
}
