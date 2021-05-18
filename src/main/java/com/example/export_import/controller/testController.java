package com.example.export_import.controller;

import com.example.export_import.config.Global;
import com.example.export_import.exception.BusinessException;
import com.example.export_import.pojo.AjaxResult;
import com.example.export_import.pojo.ProjectExcel;
import com.example.export_import.service.ImportProjectService;
import com.example.export_import.util.FileUtils;
import com.example.export_import.util.StringUtils;
import com.example.export_import.util.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class testController {
    @Autowired
    private ImportProjectService importProjectService;

    @RequestMapping("/")
    public ModelAndView getWXUrl(ModelAndView modelAndView, HttpServletRequest request) {
        modelAndView.setViewName("index");
        return modelAndView;
    }


    /**
     * 下载模板
     */
    @GetMapping("/importTemplate")
    @ResponseBody
    public AjaxResult importTemplate() {
        ExcelUtil<ProjectExcel> util = new ExcelUtil<>(ProjectExcel.class);
        return util.importTemplateExcel("任务下达模版");
    }

    @PostMapping("/importData")
    @ResponseBody
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        List<ProjectExcel> userList = null;
        try {
            ExcelUtil<ProjectExcel> util = new ExcelUtil<>(ProjectExcel.class);
            userList = util.importExcel(file.getInputStream());
        }catch (Exception e){
            return AjaxResult.warn("导入错误的模版，请重新下载模版！");
//            throw new BusinessException("导入错误的模版，请重新下载模版！");
        }
        String message = importProjectService.importUser(userList);
        return AjaxResult.success(message);
    }

    /**
     * 通用下载请求
     *
     * @param fileName 文件名称
     * @param delete 是否删除
     */
    @GetMapping("common/download")
    public void fileDownload(String fileName, Boolean delete, HttpServletResponse response, HttpServletRequest request)
    {
        try
        {
            if (!FileUtils.isValidFilename(fileName))
            {
                throw new Exception(StringUtils.format("文件名称({})非法，不允许下载。 ", fileName));
            }
            String realFileName =fileName.substring(fileName.indexOf("_") + 1);
            String filePath = Global.getDownloadPath() + fileName;

            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition",
                    "attachment;fileName=" + FileUtils.setFileDownloadHeader(request, realFileName));
            FileUtils.writeBytes(filePath, response.getOutputStream());
            if (delete)
            {
                FileUtils.deleteFile(filePath);
            }
        }
        catch (Exception e)
        {
//            log.error("下载文件失败", e);
        }
    }

}
