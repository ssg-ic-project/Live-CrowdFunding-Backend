package com.crofle.livecrowdfunding.dto.response;
import com.crofle.livecrowdfunding.domain.entity.Maker;
import com.crofle.livecrowdfunding.domain.entity.User;
import lombok.*;
import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
//관리자측 사용자 관리 > 사용자 리스트 조회
public class UserMgmResponseDTO {

    //사용자, 판매자 join해서 둘다 가지고 오기
    private Long id;
    private String memberType;
    private String name;
    private String phone;
    private String email;
    private Integer zipCode;
    private String address;
    private LocalDateTime createdAt;
    private String accountStatus;

    //User entity DTO 생성
    public static UserMgmResponseDTO fromUser(User user){
        return UserMgmResponseDTO.builder()
                .id(user.getId())
                .memberType("USER")
                .name(user.getName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .zipCode(user.getZipcode())
                .address(user.getAddress())
                .createdAt(user.getRegisteredAt())
                .accountStatus(user.getStatus().toString())
                .build();
//        return modelMapper.map(user, UserMgmResponseDTO.class);
    }

    //Maker entity DTO 생성
    public static UserMgmResponseDTO fromMaker(Maker maker){
        return UserMgmResponseDTO.builder()
                .id(maker.getId())
                .memberType("MAKER")
                .name(maker.getName())
                .phone(maker.getPhone())
                .email(maker.getEmail())
                .zipCode(maker.getZipcode())
                .address(maker.getAddress())
                .createdAt(maker.getRegisteredAt())
                .accountStatus(maker.getStatus().toString())
                .build();
    }
}