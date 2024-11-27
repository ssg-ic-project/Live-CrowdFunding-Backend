    package com.crofle.livecrowdfunding.service.serviceImpl;

    import com.crofle.livecrowdfunding.domain.entity.Maker;
    import com.crofle.livecrowdfunding.domain.enums.UserStatus;
    import com.crofle.livecrowdfunding.dto.request.MakerInfoRequestDTO;
    import com.crofle.livecrowdfunding.dto.response.BusinessVerificationResponseDTO;
    import com.crofle.livecrowdfunding.dto.response.EmploymentCertOcrResponseDTO;
    import com.crofle.livecrowdfunding.dto.response.IdCardOcrResponseDTO;
    import com.crofle.livecrowdfunding.dto.response.MakerInfoResponseDTO;
    import com.crofle.livecrowdfunding.dto.request.SaveMakerRequestDTO;
    import com.crofle.livecrowdfunding.repository.redis.AccountViewRepository;
    import com.crofle.livecrowdfunding.repository.MakerRepository;
    import com.crofle.livecrowdfunding.service.AccountService;
    import com.crofle.livecrowdfunding.service.MakerService;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.log4j.Log4j2;
    import org.modelmapper.ModelMapper;
    import org.springframework.security.crypto.password.PasswordEncoder;
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
        private final PasswordEncoder passwordEncoder;
        private final AccountService accountService;

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
            try {
                // 1. 이메일 중복 체크
                if (accountViewRepository.findByEmail(request.getEmail()).isPresent()) {
                    throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
                }

                // 2. 주민등록증 OCR 검증
                IdCardOcrResponseDTO idCardResult = accountService.verifyIdCard(request.getIdCard());
                if (!idCardResult.isSuccess()) {
                    throw new IllegalArgumentException("주민등록증 인증 실패: " +
                            (idCardResult.getErrorMessage() != null ? idCardResult.getErrorMessage() : "인증에 실패했습니다."));
                }

                // 3. 재직증명서 OCR 검증
                EmploymentCertOcrResponseDTO employmentResult = accountService.verifyEmploymentCert(request.getEmploymentCert());
                if (!employmentResult.isSuccess()) {
                    throw new IllegalArgumentException("재직증명서 인증 실패: " +
                            (employmentResult.getErrorMessage() != null ? employmentResult.getErrorMessage() : "인증에 실패했습니다."));
                }

                // 4. OCR 결과와 입력 정보 비교 검증
                if (employmentResult.getName() == null || !request.getName().equals(employmentResult.getName())) {
                    throw new IllegalArgumentException("재직증명서의 이름이 입력한 이름과 일치하지 않습니다.");
                }

                if (employmentResult.getBirthDate() == null || !request.getBirthDate().equals(employmentResult.getBirthDate().replace(".", ""))) {
                    throw new IllegalArgumentException("재직증명서의 생년월일이 입력한 생년월일과 일치하지 않습니다.");
                }

                if (employmentResult.getCompanyName() == null || !request.getCompanyName().equals(employmentResult.getCompanyName())) {
                    throw new IllegalArgumentException("재직증명서의 회사명이 입력한 회사명과 일치하지 않습니다.");
                }

                // 5. 사업자번호 검증
                BusinessVerificationResponseDTO businessResult = accountService.verifyBusinessNumber(employmentResult.getBusinessNumber());
                if (!businessResult.isSuccess()) {
                    throw new IllegalArgumentException("사업자번호 검증 실패: " +
                            (businessResult.getErrorMessage() != null ? businessResult.getErrorMessage() : "검증에 실패했습니다."));
                }

                // 6. 비밀번호 암호화
                String encodedPassword = passwordEncoder.encode(request.getPassword());

                // 7. 메이커 정보 저장
                Maker maker = Maker.builder()
                        .name(request.getName())
                        .phone(request.getPhone())
                        .business(Long.parseLong(businessResult.getBusinessNumber().replace("-", "")))
                        .email(request.getEmail())
                        .password(encodedPassword)
                        .zipcode(request.getZipcode())
                        .address(request.getAddress())
                        .detailAddress(request.getDetailAddress())
                        .registeredAt(LocalDateTime.now())
                        .status(UserStatus.활성화)
                        .build();

                makerRepository.save(maker);
                log.info("메이커 정보 저장 완료: {}", maker.getEmail());

                return request;
            } catch (Exception e) {
                log.error("메이커 등록 실패: ", e);
                if (e instanceof IllegalArgumentException) {
                    throw e;
                }
                throw new IllegalArgumentException("메이커 등록 실패: " + e.getMessage());
            }
        }
        }