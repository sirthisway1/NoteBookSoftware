package com.example.markdown_demo.service;

import com.example.markdown_demo.common.dto.LoginDTO;
import com.example.markdown_demo.common.dto.RegisterDTO;
import com.example.markdown_demo.common.dto.UserInfoDTO;
import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.entity.Users;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xmg
 * @since 2024-07-04
 */
public interface UsersService extends IService<Users> {
    UserInfoDTO getUserInfo(Integer userId)throws BusinessException;
    /**
     * 用户注册
     * @param registerDTO 包含用户注册信息的DTO对象，包括用户名、密码和邮箱地址。
     * @throws BusinessException 如果注册过程中用户名或邮箱已存在于系统中，则抛出此异常。
     */
    void register(RegisterDTO registerDTO) throws BusinessException;

    /**
     * 用户登录
     * @param loginDTO 包含用户登录信息的DTO对象，包括用户名和密码。
     * @return 返回生成的令牌，令牌用于在后续请求中验证用户身份。
     * @throws BusinessException 如果提供的登录凭证无效或不匹配，将抛出此异常。
     */
    String login(LoginDTO loginDTO) throws BusinessException;

    /**
     * 更新用户头像
     * @param userId 用户的ID
     * @param avatarFile 用户上传的头像文件
     * @throws BusinessException 如果更新过程中出现错误
     */
    void updateUserAvatar(Integer userId, MultipartFile avatarFile) throws BusinessException;

}
