package com.example.markdown_demo.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.lang.ResultType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.markdown_demo.service.FileService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileServiceImpl implements FileService {


    @Value("${file.upload.path}")
    private String fileUploadDir;

    @Value("${server.port}")
    private int port;

    @Value("${server.host}")
    private String downloadIp;

    @Override
    public Map<String, Object> uploadFile(MultipartFile[] files) {
        List<String> filePaths = new ArrayList<>();
        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();  // 文件完整名称
            String extName = FileUtil.extName(originalFilename);   // 文件后缀名
            String uniFileFlag = IdUtil.fastSimpleUUID();          // 随机生成文件名
            String fileFullName = uniFileFlag + StrUtil.DOT + extName;
            String fileUploadPath = getFileUploadPath(fileFullName);

            try {
                File uploadFile = new File(fileUploadPath);
                File parentFile = uploadFile.getParentFile();
                if (!parentFile.exists()) {
                    boolean dirsCreated = parentFile.mkdirs();
                    if (!dirsCreated) {
                        throw new BusinessException(ResultType.INTERNAL_SERVER_ERROR, "文件上传失败: 创建目录失败");
                    }
                }
                file.transferTo(uploadFile);
                String uploadPath = "http://" + downloadIp + ":" + port + "/WangFiles/" + fileFullName; // 文件上传后的访问网址
                filePaths.add(uploadPath);
            } catch (IOException e) {
                throw new BusinessException(ResultType.INTERNAL_SERVER_ERROR, "文件上传失败: IO异常");
            } catch (Exception e) {
                throw new BusinessException(ResultType.INTERNAL_SERVER_ERROR, "文件上传失败: 未知错误");
            }
        }
        return createResponseForUpload(filePaths);
    }

    private String getFileUploadPath(String fileName) {
        try {
            // 获取项目根目录路径
            String uploadDirPath = "D:/NoteFiles/Image/";
            File uploadDir = new File(uploadDirPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            return new File(uploadDir, fileName).getAbsolutePath();
        } catch (Exception e) {
            throw new BusinessException(ResultType.INTERNAL_SERVER_ERROR, "获取上传路径失败");
        }
    }

    private Map<String, Object> createResponseForUpload(List<String> filePaths) {
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("errno", 0);

        List<Map<String, String>> dataList = new ArrayList<>();
        for (String filePath : filePaths) {
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("url", filePath);
            dataList.add(dataMap);
        }

        resMap.put("data", dataList);
        return resMap;
    }


}