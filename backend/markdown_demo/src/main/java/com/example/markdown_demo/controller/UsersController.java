package com.example.markdown_demo.controller;

import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.lang.Result;
import com.example.markdown_demo.common.lang.ResultType;
import com.example.markdown_demo.common.dto.LoginDTO;
import com.example.markdown_demo.common.dto.RegisterDTO;
import com.example.markdown_demo.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UsersController {

    @Autowired
    private UsersService userService;

    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid RegisterDTO registerDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.fail(ResultType.INVALID_REQUEST_BODY);
        }
        try {
            userService.register(registerDTO.getUsername(), registerDTO.getPassword(), registerDTO.getEmail());
            return Result.success(null);
        } catch (BusinessException e) {
            return Result.fail(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            return Result.fail(ResultType.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody @Valid LoginDTO loginDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.fail(ResultType.INVALID_REQUEST_BODY);
        }
        try {
            String token = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
            return Result.success(Map.of("token", token));
        } catch (BusinessException e) {
            return Result.fail(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            return Result.fail(ResultType.INTERNAL_SERVER_ERROR);
        }
    }
}