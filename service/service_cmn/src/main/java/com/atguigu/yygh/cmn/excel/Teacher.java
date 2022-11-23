package com.atguigu.yygh.cmn.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Teacher {
    @ExcelProperty("老师编号")
    private String id;
    @ExcelProperty("老师姓名")
    private String name;
}
