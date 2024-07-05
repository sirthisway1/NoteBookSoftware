package com.example.markdown_demo.controller;

import com.example.markdown_demo.common.dto.NotebookCreateDTO;
import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.utils.JwtUtil;
import com.example.markdown_demo.service.NotebooksService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xmg
 * @since 2024-07-04
 */

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/notebooks")
public class NotebooksController {

    private final NotebooksService notebooksService;

    public NotebooksController(NotebooksService notebooksService) {
        this.notebooksService = notebooksService;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserNotebooks(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: No token provided");
            }
            token = token.substring(7); // Remove "Bearer " prefix
            Integer userId = Integer.parseInt(JwtUtil.validateToken(token));
            List<Integer> notebooks = notebooksService.getAllNotebookIds(userId);
            return ResponseEntity.ok(notebooks);
        } catch (JwtException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid token");
        } catch (BusinessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNotebook(@RequestBody @Valid NotebookCreateDTO createNotebookDTO,
                                            HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: No token provided");
            }
            token = token.substring(7); // Remove "Bearer " prefix
            Integer userId = Integer.parseInt(JwtUtil.validateToken(token));
            notebooksService.createNotebook(createNotebookDTO, userId);
            return ResponseEntity.ok("Notebook created successfully.");
        } catch (BusinessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (JwtException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid token");
        }
    }
}


