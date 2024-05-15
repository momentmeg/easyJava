package com.easyjava.builder;

import com.easyjava.bean.Constants;
import com.easyjava.bean.TableInfo;
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
            bw.newLine();

            bw.write("public class " + tableInfo.getBeanName() + " implements Serializable {");
            bw.newLine();
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
