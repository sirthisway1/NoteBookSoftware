package com.example.markdown_demo.service.impl;

import com.example.markdown_demo.common.dto.LoginDTO;
import com.example.markdown_demo.common.dto.RegisterDTO;
import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.lang.ResultType;
import com.example.markdown_demo.entity.Users;
import com.example.markdown_demo.mapper.UsersMapper;
import com.example.markdown_demo.service.FileService;
import com.example.markdown_demo.service.UsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.markdown_demo.common.dto.UserInfoDTO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import static com.example.markdown_demo.common.utils.JwtUtil.generateToken;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xmg
 * @since 2024-07-04
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private FileService fileService;
    @Override
    public UserInfoDTO getUserInfo(Integer userId) {
        // 从数据库中获取用户
        Users user = usersMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultType.NOT_FOUND);  // 抛出业务异常
        }
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        // 使用 Spring 的 BeanUtils 来复制属性
        BeanUtils.copyProperties(user, userInfoDTO);
        return userInfoDTO;
    }


    @Transactional
    public void register(RegisterDTO registerDTO) {
        // 使用 MyBatis-Plus 提供的方法进行查询
        if (lambdaQuery().eq(Users::getUsername, registerDTO.getUsername()).count() > 0) {
            throw new BusinessException(ResultType.USERNAME_ALREADY_EXISTS);
        }
        if (lambdaQuery().eq(Users::getEmail, registerDTO.getEmail()).count() > 0) {
            throw new BusinessException(ResultType.EMAIL_ALREADY_EXISTS);
        }

        // 创建用户实体
        Users user = new Users();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword());
        user.setEmail(registerDTO.getEmail());

        // 使用 MyBatis-Plus 提供的方法保存用户
        save(user);
    }

    @Override
    public String login(LoginDTO loginDTO) throws BusinessException {
        Users user = lambdaQuery()
                .eq(Users::getUsername, loginDTO.getUsername())
                .one();
        if (user == null || !loginDTO.getPassword().equals(user.getPassword())) {
            throw new BusinessException(ResultType.INVALID_CREDENTIALS);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // 或者您需要的任何其他格式
        String now = LocalDateTime.now().format(formatter);
        user.setUpdatedAt(now);
        updateById(user);
        // 生成并返回 token 或者其他登录成功的标识
        return generateToken(user.getId().toString());
    }


    @Override
    public void updateUserAvatar(Integer userId, MultipartFile avatarFile) throws BusinessException {
        // 调用文件服务上传头像，获取URL
        Map<String, Object> uploadResult = fileService.uploadFile(new MultipartFile[] {avatarFile});
        List<Map<String, String>> dataList = (List<Map<String, String>>) uploadResult.get("data");
        if (dataList.isEmpty()) {
            throw new BusinessException(ResultType.INTERNAL_SERVER_ERROR, "上传头像失败");
        }
        String avatarUrl = dataList.get(0).get("url");

        // 更新数据库中的用户记录
        Users user = usersMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultType.INTERNAL_SERVER_ERROR, "用户不存在");
        }
        user.setAvatar(avatarUrl);
        usersMapper.updateById(user);
    }
}
