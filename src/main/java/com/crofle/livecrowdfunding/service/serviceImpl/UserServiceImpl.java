package com.crofle.livecrowdfunding.service.serviceImpl;

import com.crofle.livecrowdfunding.domain.entity.*;
import com.crofle.livecrowdfunding.domain.id.UserCategoryId;
import com.crofle.livecrowdfunding.dto.request.SaveUserRequestDTO;
import com.crofle.livecrowdfunding.dto.request.UserInfoRequestDTO;
import com.crofle.livecrowdfunding.dto.response.UserInfoResponseDTO;
import com.crofle.livecrowdfunding.repository.CategoryRepository;
import com.crofle.livecrowdfunding.repository.MakerRepository;
import com.crofle.livecrowdfunding.repository.UserRepository;
import com.crofle.livecrowdfunding.repository.redis.AccountViewRepository;
import com.crofle.livecrowdfunding.service.UserService;
import com.crofle.livecrowdfunding.domain.enums.UserStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    private final MakerRepository makerRepository;
    private final AccountViewRepository accountViewRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserInfoResponseDTO findUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return modelMapper.map(user, UserInfoResponseDTO.class);
    }

    @Override
    @Transactional
    public void updateUser(Long id, UserInfoRequestDTO userInfoRequestDTO) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.updateUserInfo(userInfoRequestDTO);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        userRepository.delete(user);
    }

    @Override
    @Transactional
    public SaveUserRequestDTO saveUser(SaveUserRequestDTO request) {
        // 이메일 중복 체크
        if (accountViewRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // 비밀번호 암호화 추가
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .name(request.getName())
                .nickname(request.getNickname())
                .phone(request.getPhone())
                .gender(request.getGender())
                .birth(request.getBirth())
                .email(request.getEmail())
                .password(encodedPassword)
                .zipcode(request.getZipcode())
                .address(request.getAddress())
                .detailAddress(request.getDetailAddress())
                .loginMethod(request.getLoginMethod())
                .notification(request.getNotification())
                .status(UserStatus.활성화)
                .registeredAt(LocalDateTime.now())
                .build();

        user = userRepository.save(user);
        log.info("사용자 기본 정보 저장 완료");

        // 관심 카테고리 처리
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            List<UserInterest> interests = new ArrayList<>();
            for (Long categoryId : request.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + categoryId));

                UserInterest interest = UserInterest.builder()
                        .id(new UserCategoryId(user.getId(), category.getId()))
                        .user(user)
                        .category(category)
                        .build();
                interests.add(interest);
            }
            user.setInterests(interests);
            userRepository.save(user);
        }

        return request;
    }

}

