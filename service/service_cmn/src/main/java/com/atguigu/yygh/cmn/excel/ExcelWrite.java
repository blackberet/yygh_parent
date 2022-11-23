package com.atguigu.yygh.cmn.excel;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;

public class ExcelWrite {
    public static void main(String[] args) {
        String fileName = "D:\\Desktop\\student.xlsx";
        ArrayList<Student> students = new ArrayList<>();
        students.add(new Student(1,"李云龙"));
        students.add(new Student(2,"张大彪"));
        students.add(new Student(3,"楚云飞"));
        EasyExcel.write(fileName,Student.class).sheet(0).doWrite(students);
    }
}
