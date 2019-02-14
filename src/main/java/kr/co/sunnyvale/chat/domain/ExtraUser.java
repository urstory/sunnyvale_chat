package kr.co.sunnyvale.chat.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "extra_user")
@Getter
@Setter
public class ExtraUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 255)
    private String email;

    @Column(length = 255)
    private String uuid;

    private LocalDateTime regdate;
}