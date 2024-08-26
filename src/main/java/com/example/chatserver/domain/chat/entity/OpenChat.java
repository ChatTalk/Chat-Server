package com.example.chatserver.domain.chat.entity;

import com.example.chatserver.domain.chat.dto.OpenChatDTO;
import com.example.chatserver.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "openchats")
public class OpenChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User openUser;

    @Column(name = "max_personnel")
    private Integer maxPersonnel;

    @CreatedDate
    @Column(name = "created", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    public OpenChat(OpenChatDTO dto, User openUser) {
        this.title = dto.getTitle();
        this.openUser = openUser;
        this.maxPersonnel = dto.getMaxPersonnel();
    }
}
