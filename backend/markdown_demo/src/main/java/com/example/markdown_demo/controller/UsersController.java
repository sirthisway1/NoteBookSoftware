package com.example.markdown_demo.controller;

import com.example.markdown_demo.common.dto.UserInfoDTO;
import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.lang.Result;
import com.example.markdown_demo.common.lang.ResultType;
import com.example.markdown_demo.common.dto.LoginDTO;
import com.example.markdown_demo.common.dto.RegisterDTO;
import com.example.markdown_demo.common.utils.JwtUtil;
import com.example.markdown_demo.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UsersController {

    @Autowired
    private UsersService userService;

    private Integer getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("token");
        return Integer.parseInt(JwtUtil.validateToken(token));
    }

    @PostMapping("/register")
    public Result<Map<String, String>> register(@Valid @RequestBody RegisterDTO registerDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError firstError = bindingResult.getFieldError();
            if (firstError != null) {
                return Result.fail(ResultType.INVALID_REQUEST_BODY.getCode(), firstError.getDefaultMessage());
            }
        }
        userService.register(registerDTO);
        return Result.success(ResultType.SUCCESS.getMessage());
    }

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody @Valid LoginDTO loginDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError firstError = bindingResult.getFieldError();
            if (firstError != null) {
                return Result.fail(ResultType.INVALID_REQUEST_BODY.getCode(), firstError.getDefaultMessage());
            }
        }

        String token = userService.login(loginDTO);
        return Result.success(Map.of("token", token));

    }

    @GetMapping("/user")
    public Result<UserInfoDTO> getUserInfo(HttpServletRequest request) {
        try {
            Integer userId = getUserIdFromRequest(request);
            UserInfoDTO userInfo = userService.getUserInfo(userId);
            return Result.success(userInfo);
        }catch (BusinessException e) {
            return Result.fail(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }



    }

    @PostMapping("/user/avatar")
    public Result<String> updateAvatar(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        try {
            Integer userId = getUserIdFromRequest(request);

//            // 获取文件名并检查其后缀
//            String filename = file.getOriginalFilename();
//            if (filename == null || (!filename.endsWith(".png") && !filename.endsWith(".jpg") && !filename.endsWith(".jpeg"))) {
//                return Result.fail(ResultType.INTERNAL_SERVER_ERROR.getCode(), "只支持 PNG 或 JPG 文件格式");
//            }

            userService.updateUserAvatar(userId, file);
            return Result.success("头像更新成功");
        } catch (BusinessException e) {
            return Result.fail(e.getStatusCode(), e.getMessage());
        }
    }




    @PutMapping("/user/update")
    public Result<?> updateUserInfo(HttpServletRequest request, @RequestBody UserInfoDTO userInfoDTO) {
        try {
            Integer userId = getUserIdFromRequest(request);
            userService.updateUserInfo(userId, userInfoDTO);
            return Result.success("用户信息更新成功");
        } catch (BusinessException e) {
            return Result.fail(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            return Result.fail(ResultType.INTERNAL_SERVER_ERROR.getCode(), "内部服务器错误");
        }
    }

}