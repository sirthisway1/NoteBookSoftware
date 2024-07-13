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
            if (!parentFile.exists()) {
                boolean dirsCreated = parentFile.mkdirs();
                if (!dirsCreated) {
                    throw new BusinessException(ResultType.INTERNAL_SERVER_ERROR, "文件上传失败: 创建目录失败");
                }
            }
            file.transferTo(uploadFile);
        } catch (IOException e) {
            throw new BusinessException(ResultType.INTERNAL_SERVER_ERROR, "文件上传失败: IO异常");
        } catch (Exception e) {
            throw new BusinessException(ResultType.INTERNAL_SERVER_ERROR, "文件上传失败: 未知错误");
        }

        String uploadPath = "http://" + downloadIp + ":" + port + "/api/files/download/" + fileFullName; // 文件上传后的访问网址
        return createResponseForUpload(uploadPath);
    }

    private Map<String, Object> createResponseForUpload(String uploadPath) {
        Map<String, Object> resMap = new HashMap<>();
        // wangEditor上传图片成功后， 需要返回的参数
        resMap.put("errno", 0); // wangEditor需要的响应格式
        resMap.put("data", CollUtil.newArrayList(Dict.create().set("url", uploadPath)));
        return resMap;
    }

    private String getFileUploadPath(String fileName) {
        try {
            // 获取项目根目录路径
            String projectRootPath = Paths.get("").toAbsolutePath().toString();
            // 构建上传目录路径（src/main/resources/staticFile/upload）
            String uploadDirPath = projectRootPath + "/src/main/resources" + fileUploadDir;
            File uploadDir = new File(uploadDirPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            return new File(uploadDir, fileName).getAbsolutePath();
        } catch (Exception e) {
            throw new BusinessException(ResultType.INTERNAL_SERVER_ERROR, "获取上传路径失败");
        }
    }
}