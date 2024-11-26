package com.crofle.livecrowdfunding.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BlockUserRequest {
    private String blockedUserName;
}