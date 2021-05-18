package com.example.export_import.service;

import com.example.export_import.pojo.ProjectExcel;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ImportProjectService {


    public String importUser(List<ProjectExcel> userList) {
        return "导入成功";
    }
}
