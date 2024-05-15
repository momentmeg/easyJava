package com.easyjava;

import com.easyjava.bean.TableInfo;
import com.easyjava.builder.BuildPo;
import com.easyjava.builder.BuildTable;

import java.util.List;

/**
 * @FileName RunApplication
 * @Description 启动类
 * @Author fahrtwind
 * @date 2024-04-28
 **/


public class RunApplication {
    public static void main(String[] args) {
        List<TableInfo> tableInfoList = BuildTable.getTables();
        for (TableInfo tableInfo:tableInfoList){
            BuildPo.execute(tableInfo);
        }
    }
}
