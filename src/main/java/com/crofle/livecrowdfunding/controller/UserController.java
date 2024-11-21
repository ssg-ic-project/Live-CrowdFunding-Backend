package com.crofle.livecrowdfunding.controller;

import com.crofle.livecrowdfunding.dto.request.UserInfoRequestDTO;
import com.crofle.livecrowdfunding.dto.response.UserInfoResponseDTO;
import com.crofle.livecrowdfunding.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserInfoResponseDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUser(id));
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody UserInfoRequestDTO userInfoRequestDTO) {
        userService.updateUser(id, userInfoRequestDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
