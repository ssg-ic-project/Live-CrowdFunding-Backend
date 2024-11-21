    package com.crofle.livecrowdfunding.service.serviceImpl;

    import com.crofle.livecrowdfunding.domain.entity.Maker;
    import com.crofle.livecrowdfunding.domain.enums.UserStatus;
    import com.crofle.livecrowdfunding.dto.request.MakerInfoRequestDTO;
    import com.crofle.livecrowdfunding.dto.response.MakerInfoResponseDTO;
    import com.crofle.livecrowdfunding.dto.request.SaveMakerRequestDTO;
    import com.crofle.livecrowdfunding.repository.redis.AccountViewRepository;
    import com.crofle.livecrowdfunding.repository.MakerRepository;
    import com.crofle.livecrowdfunding.service.MakerService;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.log4j.Log4j2;
    import org.modelmapper.ModelMapper;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.stereotype.Service;

    import java.time.LocalDateTime;

    @Service
    @RequiredArgsConstructor
    @Log4j2
    public class MakerServiceImpl implements MakerService {

        private final MakerRepository makerRepository;
        private final AccountViewRepository accountViewRepository;
        private final ModelMapper modelMapper;

        @Override
        @Transactional
        public MakerInfoResponseDTO findMaker(Long makerId) {
            Maker maker = makerRepository.findById(makerId).orElseThrow(() -> new IllegalArgumentException("해당 메이커가 존재하지 않습니다."));

            return modelMapper.map(maker, MakerInfoResponseDTO.class);
        }

        @Override
        @Transactional
        public void updateMaker(Long id, MakerInfoRequestDTO makerInfoRequestDTO) {
            Maker maker = makerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 메이커가 존재하지 않습니다."));

            maker.updateMakerInfo(makerInfoRequestDTO);
        }

        //판매자 회원가입

            @Override
            @Transactional
            public SaveMakerRequestDTO saveMaker(SaveMakerRequestDTO request) {
                // 이메일 중복 체크
                if (accountViewRepository.findByEmail(request.getEmail()).isPresent()) {
                    throw new IllegalArgumentException("Email already exists");
                }

                Maker maker = Maker.builder()
                        .name(request.getName())
                        .phone(request.getPhone())
                        .business(request.getBusiness())
                        .email(request.getEmail())
                        .password(request.getPassword())
                        .zipcode(request.getZipcode())
                        .address(request.getAddress())
                        .detailAddress(request.getDetailAddress())
                        .registeredAt(LocalDateTime.now())
                        .status(UserStatus.활성화)
                        .build();

                makerRepository.save(maker);
                log.info("메이커 정보 저장 완료");

                return request;
            }

        }