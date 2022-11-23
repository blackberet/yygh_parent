package com.atguigu.yygh.cmn.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;

public class ExcelWrite3 {
    public static void main(String[] args) {
        String fileName = "D:\\Desktop\\student3.xlsx";
        ArrayList<Student> students = new ArrayList<>();
        students.add(new Student(1,"李云龙"));
        students.add(new Student(2,"张大彪"));
        students.add(new Student(3,"楚云飞"));


        ArrayList<Teacher> teachers = new ArrayList<>();
        teachers.add(new Teacher("1","赵刚"));
        teachers.add(new Teacher("2","丁伟"));
        teachers.add(new Teacher("3","孔捷"));


        ExcelWriter excelWriter = EasyExcel.write(fileName).build();

        WriteSheet writeSheet1 = EasyExcel.writerSheet(0,"sheet1").head(Student.class).build();
        excelWriter.write(students,writeSheet1);

        WriteSheet writeSheet2 = EasyExcel.writerSheet(1,"sheet2").head(Teacher.class).build();
        excelWriter.write(teachers,writeSheet2);

        excelWriter.finish();

    }
}
