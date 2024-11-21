package com.crofle.livecrowdfunding.controller.api;
import com.crofle.livecrowdfunding.dto.SearchTypeDTO;
import com.crofle.livecrowdfunding.dto.request.PageRequestDTO;
import com.crofle.livecrowdfunding.dto.response.PageListResponseDTO;
import com.crofle.livecrowdfunding.dto.response.UserMgmResponseDTO;
import com.crofle.livecrowdfunding.service.AdminUserMgmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api")
public class AdminUserMgmRestController {

    private final AdminUserMgmService adminUserMgmService;
    @GetMapping("/users") //회원관리 유형: 판매자, 일반회원
    public ResponseEntity<PageListResponseDTO<UserMgmResponseDTO>> getUsers(
            @RequestParam (defaultValue = "1") int page,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String memberType,
            @RequestParam(required = false) String name){
        SearchTypeDTO searchTypeDTO = SearchTypeDTO.builder()
                .US(status)
                .MT(memberType)
                .build();

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(page)
                .search(searchTypeDTO)
                .userName(name)
                .build();

        PageListResponseDTO<UserMgmResponseDTO> data = adminUserMgmService.getAllMembers(pageRequestDTO);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}