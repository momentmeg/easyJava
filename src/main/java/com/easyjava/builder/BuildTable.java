package com.easyjava.builder;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.easyjava.Utils.JsonUtils;
import com.easyjava.Utils.PropertiesUtils;
import com.easyjava.Utils.StringUtils;
import com.easyjava.bean.Constants;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @FileName BuildTable
 * @Description
 * @Author fahrtwind
 * @date 2024-04-28
 **/


public class BuildTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(BuildTable.class);
    private static Connection conn = null;

    private static String SQL_SHOW_TABLE_STATUS = "show table status";

    private static String SQL_SHOW_TABLE_FIELDS = "show full fields from %s";

    private static String SQL_SHOW_TABLE_INDEX = "show index from %s";

    static {
        String driver = PropertiesUtils.getString("name");
        String url = PropertiesUtils.getString("url");
        String username = PropertiesUtils.getString("username");
        String password = PropertiesUtils.getString("password");
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            LOGGER.error("数据库连接失败", e);
        }
    }


    public static List<TableInfo> getTables() {
        PreparedStatement ps = null;
        ResultSet tableResult = null;

        List<TableInfo> tableInfoList = new ArrayList();
        try {
            ps = conn.prepareStatement(SQL_SHOW_TABLE_STATUS);
            tableResult = ps.executeQuery();
            while (tableResult.next()) {
                String tableName = tableResult.getString("name");
                String comment = tableResult.getString("comment");
//                LOGGER.info("tableName:{},comment:{}",tableName,comment);


                String beanName = tableName;
                if (Constants.IGNORE_TABLE_PREFIX) {
                    beanName = tableName.substring(beanName.indexOf("_") + 1);
                }
                beanName = processFiled(beanName, true);

                TableInfo tableInfo = new TableInfo();
                tableInfo.setTableName(tableName);
                tableInfo.setBeanName(beanName);
                tableInfo.setComment(comment);
                tableInfo.setBeanParamName(beanName + Constants.SUFFIX_BEAN_PARAM);

                readFieldInfo(tableInfo);

                getKeyIndexInfo(tableInfo);

                tableInfoList.add(tableInfo);
            }
        } catch (Exception e) {
            LOGGER.error("读取表失败", e);
        } finally {

            try {
                if (tableResult != null) {
                    tableResult.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return tableInfoList;
    }


    private static String processFiled(String field, Boolean upperCaseFirstLetter) {
        StringBuffer sb = new StringBuffer();
        String[] fields = field.split("_");
        sb.append(upperCaseFirstLetter ? StringUtils.upperCaseFirstLetter(fields[0]) : fields[0]);
        for (int i = 1, len = fields.length; i < len; i++) {
            sb.append(StringUtils.upperCaseFirstLetter(fields[i]));
        }
        return sb.toString();
    }


    private static void readFieldInfo(TableInfo tableInfo) {
        PreparedStatement ps = null;
        ResultSet fieldResults = null;

        List<FieldInfo> fieldInfoList = new ArrayList();
        try {
            ps = conn.prepareStatement(String.format(SQL_SHOW_TABLE_FIELDS, tableInfo.getTableName()));
            fieldResults = ps.executeQuery();
            while (fieldResults.next()) {
                String field = fieldResults.getString("field");
                String type = fieldResults.getString("type");
                String extra = fieldResults.getString("extra");
                String comment = fieldResults.getString("comment");

                if (type.indexOf("(") > 0) {
                    type = type.substring(0, type.indexOf("("));
                }
                String propertyName = processFiled(field, false);

                FieldInfo fieldInfo = new FieldInfo();
                fieldInfoList.add(fieldInfo);


                fieldInfo.setFieldName(field);
                fieldInfo.setComment(comment);
                fieldInfo.setSqlType(type);
                fieldInfo.setAutoIncrement("auto_increment".equalsIgnoreCase(extra) ? true : false);
                fieldInfo.setPropertyName(propertyName);
                fieldInfo.setJavaType(processJavaType(type));

                if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES,type)){
                    tableInfo.setHaveDateTime(true);
                } else {
                    tableInfo.setHaveDateTime(false);
                }
                if (ArrayUtils.contains(Constants.SQL_DATE_TYPES,type)){
                    tableInfo.setHaveDate(true);
                }else {
                    tableInfo.setHaveDate(false);
                }
                if (ArrayUtils.contains(Constants.SQL_DECIMAL_TYPE,type)){
                    tableInfo.setHaveBigDecimal(true);
                }else {
                    tableInfo.setHaveBigDecimal(false);
                }

            }

            tableInfo.setFieldList(fieldInfoList);

        } catch (Exception e) {
            LOGGER.error("读取表失败", e);
        } finally {

            try {
                if (fieldResults != null) {
                    fieldResults.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }


    private static String processJavaType(String type) {
        if (ArrayUtils.contains(Constants.SQL_INTEGER_TYPE, type)) {
            return "Integer";
        } else if (ArrayUtils.contains(Constants.SQL_LONG_TYPE, type)) {
            return "Long";
        } else if (ArrayUtils.contains(Constants.SQL_STRING_TYPE, type)) {
            return "String";
        } else if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, type) || ArrayUtils.contains(Constants.SQL_DATE_TYPES, type)) {
            return "Date";
        } else if (ArrayUtils.contains(Constants.SQL_BOOLEAN_TYPE, type)) {
            return "Boolean";
        } else if (ArrayUtils.contains(Constants.SQL_DECIMAL_TYPE, type)) {
            return "BigDecimal";
        } else {
            throw new RuntimeException("无法识别此类型：" + type);
        }
    }


    private static List<FieldInfo> getKeyIndexInfo(TableInfo tableInfo) {
        PreparedStatement ps = null;
        ResultSet fieldResults = null;

        List<FieldInfo> fieldInfoList = new ArrayList();
        try {

            Map<String,FieldInfo> tempMap = new HashMap();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()){
               tempMap.put(fieldInfo.getFieldName(),fieldInfo);
            }

            ps = conn.prepareStatement(String.format(SQL_SHOW_TABLE_INDEX, tableInfo.getTableName()));
            fieldResults = ps.executeQuery();
            while (fieldResults.next()) {
                String keyName = fieldResults.getString("key_name");
                Integer nonUnique = fieldResults.getInt("non_unique");
                String columnName = fieldResults.getString("column_name");

                if(nonUnique == 1){
                    continue;
                }
                List<FieldInfo> keyFieldList = tableInfo.getKeyIndexMap().get(keyName);
                if(keyFieldList == null){
                    keyFieldList = new ArrayList();
                    tableInfo.getKeyIndexMap().put(keyName,keyFieldList);
                }
//                for (FieldInfo fieldInfo : tableInfo.getFieldList()){
//                    if(fieldInfo.getFieldName().equals(columnName)){
//                        keyFieldList.add(fieldInfo);
//                    }
//                }
                keyFieldList.add(tempMap.get(columnName));
            }
        } catch (Exception e) {
            LOGGER.error("读取索引失败", e);
        } finally {

            try {
                if (fieldResults != null) {
                    fieldResults.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return fieldInfoList;
    }

}
