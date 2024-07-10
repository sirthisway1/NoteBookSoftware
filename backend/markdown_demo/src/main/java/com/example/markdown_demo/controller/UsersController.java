package com.example.markdown_demo.controller;

import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.lang.Result;
import com.example.markdown_demo.common.lang.ResultType;
import com.example.markdown_demo.common.dto.LoginDTO;
import com.example.markdown_demo.common.dto.RegisterDTO;
import com.example.markdown_demo.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UsersController {

    @Autowired
    private UsersService userService;

    @PostMapping("/register")
    public Result<Map<String, String>> register(@Valid @RequestBody RegisterDTO registerDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError firstError = bindingResult.getFieldError();
            if (firstError != null) {
                return Result.fail(ResultType.INVALID_REQUEST_BODY.getCode(), firstError.getDefaultMessage());
            }
        }
        userService.register(registerDTO);
        return Result.success(null);
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
}