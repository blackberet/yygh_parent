package com.atguigu.yygh.cmn.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;

import java.util.ArrayList;

public class ExcelWrite2 {
    public static void main(String[] args) {
        String fileName = "D:\\Desktop\\student1.xlsx";
        ArrayList<Student> students = new ArrayList<>();
        students.add(new Student(1,"李云龙"));
        students.add(new Student(2,"张大彪"));
        students.add(new Student(3,"楚云飞"));
        ExcelWriter excelWriter = EasyExcel.write(fileName, Student.class).build();
        for (int i = 0; i < 3; i++) {
            WriteSheet writeSheet = EasyExcel.writerSheet(i,"sheet" + i + 1).build();
            excelWriter.write(students,writeSheet);
        }
        excelWriter.finish();

    }
}
