package com.crofle.livecrowdfunding.service;

import com.crofle.livecrowdfunding.dto.request.MakerInfoRequestDTO;
import com.crofle.livecrowdfunding.dto.response.MakerInfoResponseDTO;
import com.crofle.livecrowdfunding.dto.request.SaveMakerRequestDTO;

public interface MakerService {
    MakerInfoResponseDTO findMaker(Long makerId);
    void updateMaker(Long id, MakerInfoRequestDTO makerInfoRequestDTO);
    SaveMakerRequestDTO saveMaker(SaveMakerRequestDTO saveMakerRequestDTO);

}