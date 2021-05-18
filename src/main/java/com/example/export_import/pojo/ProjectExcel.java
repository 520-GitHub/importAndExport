package com.example.export_import.pojo;

import com.example.export_import.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ProjectExcel {
    private static final long serialVersionUID = 1L;
    @Excel(name = "年度", prompt = "提示：20XX")
    private Integer year;
    @Excel(name = "项目名称")
    private String projectName;
    @Excel(name = "项目预算金额", prompt = "请输入少于十个字符的纯数字")
    private BigDecimal projectBudgetAmount;
    @Excel(name = "项目资金性质", prompt = "可选：一般公共预算、政府性基金预算、国有资本经营预算、社会保险基金预算")
    private String natureProjectFunds;
    @Excel(name = "功能科目名称", prompt = "项目资金性质:一般公共预算|政府性基金预算时必填，多个请用逗号“,”隔开。其余选项填“无”")
    private String functionalName;
    @Excel(name = "社会保险类型", prompt = "若项目资金性质为社会保险基金预算时可选：居民|生育|失业|工伤|机关养老|职工，否则填“无”")
    private String type;
    @Excel(name = "创建任务日期", prompt = "格式：yyyy-MM-dd 如2020-01-01")
    private Date craTime;
}
