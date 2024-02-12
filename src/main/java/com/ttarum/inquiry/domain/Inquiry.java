package com.ttarum.inquiry.domain;

import com.ttarum.common.domain.BaseEntity;
import com.ttarum.item.domain.Item;
import com.ttarum.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "inquiry")
public class Inquiry extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "int")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false, columnDefinition = "int")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false, columnDefinition = "int")
    private Item item;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Column(name = "exist_answer", nullable = false)
    private Boolean existAnswer;

    @Column(name = "is_secret", nullable = false)
    private Boolean isSecret;

    @PrePersist
    public void prePersist() {
        this.existAnswer = false;
    }
}