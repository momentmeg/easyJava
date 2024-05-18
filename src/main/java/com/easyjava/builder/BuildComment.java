package com.easyjava.builder;

import com.easyjava.Utils.DateUtils;
import com.easyjava.bean.Constants;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;

/**
 * @FileName BuildComment
 * @Description
 * @Author fahrtwind
 * @date 2024-05-18
 **/


public class BuildComment {
    public static void createClassComment(BufferedWriter bw, String classComment) throws IOException {
        bw.write("/**");
        bw.newLine();
        bw.write(" * @Description:" + classComment);
        bw.newLine();
        bw.write(" * @Author:"+ Constants.AUTHOR_COMMENT);
        bw.newLine();
        bw.write(" * @Data:"+ DateUtils.format(new Date(), DateUtils.style2));
        bw.newLine();
        bw.write(" **/");
        bw.newLine();

    }

    public static void createFieldComment(BufferedWriter bw, String fieldComment) throws IOException {
        bw.newLine();
        bw.write("\t/**");
        bw.newLine();
        if (fieldComment != ""){
            bw.write("\t * comment:"+fieldComment);
        }else {
            bw.write("\t * comment:");
        }
        bw.newLine();
        bw.write("\t **/");
        bw.newLine();

    }



    public static void createMethodComment() {
    }



}
