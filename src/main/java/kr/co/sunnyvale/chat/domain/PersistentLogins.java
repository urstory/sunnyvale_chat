package kr.co.sunnyvale.chat.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "persistent_login")
@Getter
@Setter
public class PersistentLogins {
    @Id
    @Column(length = 64)
    private String series;
    @Column(length = 64)
    private String username;
    @Column(length = 64)
    private String token;
    @Column(name = "last_used")
    private Date lastUsed;
}