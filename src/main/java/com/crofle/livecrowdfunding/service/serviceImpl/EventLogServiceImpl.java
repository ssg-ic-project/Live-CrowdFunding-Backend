package com.crofle.livecrowdfunding.service.serviceImpl;

import com.crofle.livecrowdfunding.dto.PageInfoDTO;
import com.crofle.livecrowdfunding.dto.response.EventLogWithEventNameResponseDTO;
import com.crofle.livecrowdfunding.dto.request.PageRequestDTO;
import com.crofle.livecrowdfunding.dto.response.PageListResponseDTO;
import com.crofle.livecrowdfunding.repository.EventLogRepository;
import com.crofle.livecrowdfunding.service.EventLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class EventLogServiceImpl implements EventLogService {
    private final EventLogRepository eventLogRepository;

    @Override
    public PageListResponseDTO<EventLogWithEventNameResponseDTO> findByUser(Long userId, PageRequestDTO pageRequestDTO) {
        Page<EventLogWithEventNameResponseDTO> eventLogWithEventNameDTOS = eventLogRepository.findByUser(userId, pageRequestDTO.getPageable());

        return PageListResponseDTO.<EventLogWithEventNameResponseDTO>builder()
                .dataList(eventLogWithEventNameDTOS.getContent())
                .pageInfoDTO(PageInfoDTO.withAll()
                        .pageRequestDTO(pageRequestDTO)
                        .total((int)eventLogWithEventNameDTOS.getTotalElements())
                        .build())
                .build();
    }
}
