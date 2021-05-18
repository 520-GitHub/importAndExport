package com.example.export_import.util.poi;


import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;

public class exportword {

    public void exportWord(HttpServletRequest request, HttpServletResponse response, String html, String title) {
        ServletOutputStream ostream = null;
        POIFSFileSystem poifs = null;
        ByteArrayInputStream bais = null;
        try {
            //word内容
            String content = html;
            byte b[] = content.getBytes("utf-8");  //这里是必须要设置编码的，不然导出中文就会乱码。
            bais = new ByteArrayInputStream(b);//将字节数组包装到流中

            /* 关键地方、成word格式 */
            poifs = new POIFSFileSystem();
            DirectoryEntry directory = poifs.getRoot();
            DocumentEntry documentEntry = directory.createDocument("WordDocument", bais);
            //输出文件
            request.setCharacterEncoding("utf-8");
            response.setContentType("application/msword");//导出word格式
            response.addHeader("Content-Disposition", "attachment;filename=" +
                    new String(title.getBytes("GB2312"), "iso8859-1") + ".doc");
            ostream = response.getOutputStream();
            poifs.writeFilesystem(ostream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bais != null) {
                    bais.close();
                }
                if (ostream != null) {
                    ostream.close();

                }
                if (poifs != null) {
                    poifs.close();
                }
            } catch (Exception ex) {
            }
        }
    }


}
