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
}