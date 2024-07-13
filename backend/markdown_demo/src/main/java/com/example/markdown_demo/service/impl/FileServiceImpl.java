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
import java.util.HashMap;
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
    public Map<String, Object> uploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();  // 文件完整名称
        String extName = FileUtil.extName(originalFilename);   // 文件后缀名
        String uniFileFlag = IdUtil.fastSimpleUUID();          // 随机生成文件名
        String fileFullName = uniFileFlag + StrUtil.DOT + extName;
        String fileUploadPath = getFileUploadPath(fileFullName);
        try {
            File uploadFile = new File(fileUploadPath);
            File parentFile = uploadFile.getParentFile();
            if (!parentFile.exists()) { // 如果父级目录不存在，就是files目录不存在，就创建files目录
                parentFile.mkdirs();
            }
            file.transferTo(uploadFile);
        } catch (Exception e) {
            throw new BusinessException(ResultType.INTERNAL_SERVER_ERROR,"文件上传失败");
        }
        String uploadPath = "http://" + downloadIp + ":" + port + "/api/files/download/" + fileFullName; // 文件上传后的访问网址

        Map<String, Object> resMap = new HashMap<>();
        // wangEditor上传图片成功后， 需要返回的参数
        resMap.put("errno", 0);
        resMap.put("data", CollUtil.newArrayList(Dict.create().set("url", uploadPath)));
        return resMap;
    }

    private String getFileUploadPath(String fileName) {
        return fileUploadDir + File.separator + fileName;
    }
}
