package com.crofle.livecrowdfunding.domain.entity;

import com.crofle.livecrowdfunding.domain.id.UserCategoryId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "USER_INTEREST")
@Getter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserInterest {
    @EmbeddedId
    private UserCategoryId id;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;





}
