package com.crofle.livecrowdfunding.domain.id;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class UserCategoryId implements Serializable {
    private Long userId;
    private Long categoryId;
}
