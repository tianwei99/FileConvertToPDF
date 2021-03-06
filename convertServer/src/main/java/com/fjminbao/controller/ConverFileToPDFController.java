package com.fjminbao.controller;

import com.fjminbao.dto.ResponseDTO;
import com.fjminbao.task.config.ConfigProperties;
import com.fjminbao.task.service.AsyncService;
import com.fjminbao.util.DownLoadFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/**
 * 处理异步请求
 * @Author: xianyuanLi
 * @Date: created in 15:01 2019/12/20
 * Descrpition:  去除了将源文件从Linux中下载到Windows中的操作(原先是为了保存源文件)；通过直接将源文件转为PDF(改成了不需要保存源文件，只保存转换后的PDF文件，缩短了时间)
 */
@RestController
public class ConverFileToPDFController {
    private static final Logger logger = Logger.getLogger("ConverFileToPDFController");

    @Autowired
    private ConfigProperties configProperties;

    @Autowired
    private  AsyncService asyncService;


    /**
     * office文件转为pdf，并获取总页数
     * @param request
     * @return 结果码:-1 文件转换失败，-2 不支持该类型文件转换；-3 参数为空
     */
    @RequestMapping(value="/convertToPdf",method = RequestMethod.POST)
    @ResponseBody
    public ResponseDTO convertToPdf(HttpServletRequest request) throws IOException {
        ResponseDTO responseDTO = new ResponseDTO();
        String fileName = request.getParameter("fileDestPathName");
        logger.info("接收到的参数:fileName="+fileName);
        if (StringUtils.isEmpty(fileName)){
            responseDTO.setResultCode(-3);
            responseDTO.setResultMsg("文件名参数为空,PARAMETER_ERROR");
            return  responseDTO;
        }
        //获取源文件路径
        String originFilePath = "http://www.fjminbaoscp.com:8085/upload/";//configProperties.getOriginFilePath();
        String originFleFullPath = originFilePath + fileName;//源文件全路径
        logger.info("源文件全路径：originFleFullPath"+originFleFullPath);
        //获取文件名
        String originFileName = fileName.substring(fileName.lastIndexOf("/")+1,fileName.length());;
        //使用http下载文件
//        DownLoadFileUtil.downLoadFromUrl(originFleFullPath,originFileName,"C:\\upload\\");



        //目标文件存放的地址
        String targetFilePath =  "K:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\"+fileName;//originFleFullPath;//"C:\\upload\\";//configProperties.getTargetFilePath();
        //拼接目标文件全路径(例如: /home/sharedprint/upload/1.doc)
//        String targetFileFullPath = targetFilePath +originFileName;
//        logger.info("目标文件全路径:"+targetFileFullPath);
        logger.info("目标文件全路径:"+targetFilePath);
        //转换后的PDF文件地址
        // 获取当前时间年月日
        Date dd = new Date();
        // 格式化
        SimpleDateFormat sim = new SimpleDateFormat("yyyyMMdd");
        String currentTime = sim.format(dd);
        File uploadFileNowdate = new File("K:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\convertToPdfDir\\" + currentTime + "\\P\\");
//        File uploadFileNowdate = new File("D:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\convertToPdfDir\\" + currentTime + "\\P\\");
        if (!uploadFileNowdate.exists() && !uploadFileNowdate.isDirectory()) {
            uploadFileNowdate.mkdirs();
        }

        String convertFilePath = "K:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\convertToPdfDir\\"+currentTime+"\\P\\";;//"C:\\upload\\";
//        String convertFilePath = "D:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\convertToPdfDir\\"+currentTime+"\\P\\";;//"C:\\upload\\";
        //转换后的PDF文件全路径(例如: /home/sharedprint/upload/1.pdf)
        String convertFileFullPath = convertFilePath  + originFileName.substring(0,originFileName.lastIndexOf(".")+1)+"pdf";
        logger.info("转换后的文件全路径:convertFileFullPath="+convertFileFullPath);

        //异步执行
//        asyncService.handleFileConvert2Pdf(targetFileFullPath,convertFileFullPath,originFileName);
        asyncService.handleFileConvert2Pdf(targetFilePath,convertFileFullPath,originFileName);
        return responseDTO;
    }





}
