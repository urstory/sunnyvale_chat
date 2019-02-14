package kr.co.sunnyvale.chat.repository;

import kr.co.sunnyvale.chat.domain.User;
import kr.co.sunnyvale.chat.domain.UserConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "select u from User u join fetch  u.roles where u.id = :userId")
    public User getUser(@Param("userId")Long userId);

    @Query(value = "select u from User u join fetch  u.roles where u.email = :email")
    public User getUser(@Param("email")String email);

    @Query(value = "select u from User u where ?1 member of u.userConnections")
    public User getFindByUserConnections(UserConnection userConnection);

    @Query(value = "select u from User u join fetch  u.roles where u.nickName = :nickName")
    User getUserByNickName(@Param("nickName")String nickName);

}
