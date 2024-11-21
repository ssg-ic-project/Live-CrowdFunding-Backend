package com.crofle.livecrowdfunding.service.serviceImpl;

import com.crofle.livecrowdfunding.domain.entity.Maker;
import com.crofle.livecrowdfunding.domain.entity.User;
import com.crofle.livecrowdfunding.dto.PageInfoDTO;
import com.crofle.livecrowdfunding.dto.request.PageRequestDTO;
import com.crofle.livecrowdfunding.dto.response.PageListResponseDTO;
import com.crofle.livecrowdfunding.dto.response.UserMgmResponseDTO;
import com.crofle.livecrowdfunding.repository.MakerRepository;
import com.crofle.livecrowdfunding.repository.UserRepository;
import com.crofle.livecrowdfunding.service.AdminUserMgmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class AdminUserMgmServiceImpl implements AdminUserMgmService {
    private final UserRepository userRepository;
    private final MakerRepository makerRepository;

    //User, Maker 둘다 가지고 오기
    public PageListResponseDTO<UserMgmResponseDTO> getAllMembers(PageRequestDTO pageRequestDTO){
        List<UserMgmResponseDTO> result = new ArrayList<>();
        int totalElements = 0;

        //1. Pageable object 로 변환하기
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage()-1, pageRequestDTO.getSize());

        //2. 회원 유형에 따른 조회
        if(pageRequestDTO.getSearch().getMT() == null || pageRequestDTO.getSearch().getMT().equalsIgnoreCase("USER")){
            Page<User> userPage = userRepository.findByConditions(pageRequestDTO, pageable);
            result.addAll(userPage.getContent().stream()
                    .map(UserMgmResponseDTO::fromUser)
                    .collect(Collectors.toList()));
            totalElements += userPage.getTotalElements();
        }
        if(pageRequestDTO.getSearch().getMT() == null || pageRequestDTO.getSearch().getMT().equalsIgnoreCase("MAKER")){
            Page<Maker> makerPage = makerRepository.findByConditions(pageRequestDTO, pageable);
            result.addAll(makerPage.getContent().stream()
                    .map(UserMgmResponseDTO::fromMaker)
                    .collect(Collectors.toList()));
            totalElements += makerPage.getTotalElements();
        }
        PageInfoDTO pageInfoDTO = PageInfoDTO.withAll().pageRequestDTO(pageRequestDTO).total(totalElements)
                .build();

        return PageListResponseDTO.<UserMgmResponseDTO>builder()
                .pageInfoDTO(pageInfoDTO)
                .dataList(result)
                .build();
    }
}