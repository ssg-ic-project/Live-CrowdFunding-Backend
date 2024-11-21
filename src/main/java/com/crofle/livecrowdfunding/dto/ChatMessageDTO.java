package com.crofle.livecrowdfunding.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {
    private String roomId;
    private String userName;
    private String content;
    private String timestamp;
}
