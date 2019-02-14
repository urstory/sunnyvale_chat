package kr.co.sunnyvale.chat.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false, unique = true)
    private String nickName;
    @Column(length = 20, nullable = false)
    private String name;

    @JsonIgnore
    @Column(length = 255, nullable = true)
    private String password;
    @Column(length = 255, nullable = true, unique = true)
    private String email;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id") ,
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id") )
    private Set<Role> roles;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserConnection> userConnections;

    public static User signUp(UserConnection userConnection){
        User user = new User();
        if(userConnection.getEmail() != null)
            user.setEmail(userConnection.getEmail());
        else
            user.setEmail(userConnection.getProviderId()); // email이 없을 경우엔 일단 ProviderId로 설정한다.
        user.setName(userConnection.getDisplayName());
        user.setNickName(userConnection.getDisplayName());
        user.addUserConnection(userConnection);
        return user;
    }

    public void addRole(Role role){
        if(roles == null){
            roles = new HashSet<>();
        }
        roles.add(role);
    }

    public void addUserConnection(UserConnection userConnection){
        userConnection.setUser(this);
        if(userConnections == null){
            userConnections = new HashSet<>();
        }
        userConnections.add(userConnection);

    }
}
