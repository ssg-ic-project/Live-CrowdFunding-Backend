package com.crofle.livecrowdfunding.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO implements Serializable {
    private String roomId;
    private String userName;
    private String content;
    private String timestamp;
}
