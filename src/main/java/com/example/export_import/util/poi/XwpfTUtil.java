package com.example.export_import.util.poi;

import org.apache.poi.xwpf.usermodel.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XwpfTUtil {
    /**
     * 替换段落里面的变量
     *
     * @param doc    要替换的文档
     * @param params 参数
     */
    public void replaceInPara(XWPFDocument doc, Map<String, Object> params) {
        Iterator<XWPFParagraph> iterator = doc.getParagraphsIterator();
        XWPFParagraph para;
        while (iterator.hasNext()) {
            para = iterator.next();
            this.replaceInPara(para, params);
        }
    }

    /**
     * 替换段落里面的变量
     *
     * @param para   要替换的段落
     * @param params 参数
     */
    public void replaceInPara(XWPFParagraph para, Map<String, Object> params) {
        List<XWPFRun> runs;
        Matcher matcher;
        if (this.matcher(para.getParagraphText()).find()) {
            runs = para.getRuns();

            int start = -1;
            int end = -1;
            String str = "";
            for (int i = 0; i < runs.size(); i++) {
                XWPFRun run = runs.get(i);
                String runText = run.toString();
                System.out.println("------>>>>>>>>>" + runText);
                if ('$' == runText.charAt(0)&&'{' == runText.charAt(1)) {
                    start = i;
                }
                if ((start != -1)) {
                    str += runText;
                }
                if ('}' == runText.charAt(runText.length() - 1)) {
                    if (start != -1) {
                        end = i;
                        break;
                    }
                }
            }
            System.out.println("start--->"+start);
            System.out.println("end--->"+end);

            System.out.println("str---->>>" + str);

            for (int i = start; i <= end; i++) {
                para.removeRun(i);
                i--;
                end--;
                System.out.println("remove i="+i);
            }

            for (String key : params.keySet()) {
                if (str.equals(key)) {
                    para.createRun().setText((String) params.get(key));
                    break;
                }
            }


        }
    }

    /**
     * 替换表格里面的变量
     *
     * @param doc    要替换的文档
     * @param params 参数
     */
    public void replaceInTable(XWPFDocument doc, Map<String, Object> params) {
        Iterator<XWPFTable> iterator = doc.getTablesIterator();
        XWPFTable table;
        List<XWPFTableRow> rows;
        List<XWPFTableCell> cells;
        List<XWPFParagraph> paras;
        while (iterator.hasNext()) {
            table = iterator.next();
            rows = table.getRows();
            for (XWPFTableRow row : rows) {
                cells = row.getTableCells();
                for (XWPFTableCell cell : cells) {
                    paras = cell.getParagraphs();
                    for (XWPFParagraph para : paras) {
                        this.replaceInPara(para, params);
                    }
                }
            }
        }
    }

    /**
     * 正则匹配字符串
     *
     * @param str
     * @return
     */
    private Matcher matcher(String str) {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        return matcher;
    }

    /**
     * 关闭输入流
     *
     * @param is
     */
    public void close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭输出流
     *
     * @param os
     */
    public void close(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /*
    *
    * */
    public void exportword(HttpServletRequest request, HttpServletResponse response, String fieldname, String modlename, Object... domains){
        XWPFDocument doc = null;
        InputStream is=null;
        OutputStream os = null;
        XwpfTUtil xwpfTUtil = new XwpfTUtil();
        Map<String, Object> params = new HashMap<String, Object>();
        try{for (Object domian :domains){


            Field[] fields = domian.getClass().getDeclaredFields();
            for(Field field : fields) {
                field.setAccessible(true);
                String name=field.getName();
                Object values=field.get(domian);
                if (values!=null){
                    params.put("${"+name+"}",values);}
            }}
            String fileNameInResource=fieldname;//word模版的名字
            is = getClass().getClassLoader().getResourceAsStream(fileNameInResource);//会在跟路径下面查看temp.doc文件。(web项目中为classes文件下面)
            doc = new XWPFDocument(is);
            xwpfTUtil.replaceInPara(doc, params);
            //替换表格里面的变量
            xwpfTUtil.replaceInTable(doc, params);
            os = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");
            //解决导出文件名为中文
            String uncod= URLDecoder.decode(modlename,"UTF-8");
            String fileName = new String(uncod.getBytes("UTF-8"), "iso-8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=".concat(String.valueOf(fileName)));
            doc.write(os);
        }catch (Exception e){
        }finally {
            xwpfTUtil.close(os);
            xwpfTUtil.close(is);
            try {
                os.flush();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}

