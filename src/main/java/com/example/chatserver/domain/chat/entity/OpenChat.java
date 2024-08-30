package com.example.chatserver.domain.chat.entity;

import com.example.chatserver.domain.chat.dto.OpenChatDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "openchats")
@EntityListeners(AuditingEntityListener.class)
public class OpenChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "open_username")
    private String openUsername;

    @Column(name = "max_personnel")
    private Integer maxPersonnel;

    @CreatedDate
    @Column(name = "created", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    public OpenChat(OpenChatDTO dto, String openUsername) {
        this.title = dto.getTitle();
        this.openUsername = openUsername;
        this.maxPersonnel = dto.getMaxPersonnel();
    }
}
