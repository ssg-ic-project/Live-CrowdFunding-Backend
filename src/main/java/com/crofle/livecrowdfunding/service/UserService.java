package com.crofle.livecrowdfunding.service;

import com.crofle.livecrowdfunding.domain.entity.AccountView;
import com.crofle.livecrowdfunding.dto.request.SaveMakerRequestDTO;
import com.crofle.livecrowdfunding.dto.request.SaveUserRequestDTO;
import com.crofle.livecrowdfunding.dto.request.UserInfoRequestDTO;
import com.crofle.livecrowdfunding.dto.response.UserInfoResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserService {

     UserInfoResponseDTO findUser(Long userId);

     void updateUser(Long id, UserInfoRequestDTO userInfoRequestDTO);

     void deleteUser(Long userId);


     SaveUserRequestDTO saveUser(SaveUserRequestDTO saveUserRequestDTO);

;
}