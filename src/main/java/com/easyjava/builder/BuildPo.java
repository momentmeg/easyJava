package com.easyjava.builder;

import com.easyjava.Utils.DateUtils;
import com.easyjava.bean.Constants;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @FileName BuildPo
 * @Description
 * @Author fahrtwind
 * @date 2024-05-02
 **/


public class BuildPo {
    private static final Logger LOGGER = LoggerFactory.getLogger(BuildPo.class);

    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_PO);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File pofile = new File(folder, tableInfo.getBeanName() + ".java");

        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(pofile);
            outw = new OutputStreamWriter(out, "utf8");
            bw = new BufferedWriter(outw);

            bw.write("package " + Constants.PACKAGE_PO + ";");
            bw.newLine();
            bw.newLine();

            bw.write("import java.io.Serializable;");
            bw.newLine();
            //导包

            if (tableInfo.getHaveDate() || tableInfo.getHaveDateTime())
            {
                bw.write("import java.util.Data;");
                bw.newLine();
                bw.write(Constants.BEAN_DATE_FORMAT_CLASS+";");
                bw.newLine();
                bw.write(Constants.BEAN_DATE_UNFORMAT_CLASS+";");
                bw.newLine();
            }
            if (tableInfo.getHaveBigDecimal()){
                bw.write("import java.math.BigDecimal;");
            }
            bw.newLine();
            bw.newLine();

            //创建注释
            BuildComment.createClassComment(bw, tableInfo.getComment());

            bw.write("public class " + tableInfo.getBeanName() + " implements Serializable {");
            bw.newLine();

            //生成属性
            for (FieldInfo field : tableInfo.getFieldList()) {
                BuildComment.createFieldComment(bw,field.getComment());

                if(ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES,field.getSqlType())){
                    //序列化
                    bw.write("\t"+String.format(Constants.BEAN_DATE_FORMAT_EXPRESSION, DateUtils.style4));
                    bw.newLine();
                    //反序列化
                    bw.write("\t"+String.format(Constants.BEAN_DATE_UNFORMAT_EXPRESSION, DateUtils.style4));
                    bw.newLine();
                }
                if(ArrayUtils.contains(Constants.SQL_DATE_TYPES,field.getSqlType())){
                    //序列化
                    bw.write("\t"+String.format(Constants.BEAN_DATE_FORMAT_EXPRESSION, DateUtils.style3));
                    bw.newLine();
                    //反序列化
                    bw.write("\t"+String.format(Constants.BEAN_DATE_UNFORMAT_EXPRESSION, DateUtils.style3));
                    bw.newLine();
                }


                bw.write("\tprivate " + field.getJavaType() + " " + field.getPropertyName() + ";");
                bw.newLine();
            }
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            LOGGER.error("创建po失败", e);

        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (outw != null) {
                try {
                    outw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }


    }
}
