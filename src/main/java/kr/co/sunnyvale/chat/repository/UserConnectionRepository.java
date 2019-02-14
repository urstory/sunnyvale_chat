package kr.co.sunnyvale.chat.repository;

import kr.co.sunnyvale.chat.domain.ProviderType;
import kr.co.sunnyvale.chat.domain.UserConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserConnectionRepository extends JpaRepository<UserConnection, Long> {

    @Query(value = "select uc from UserConnection uc where uc.provider = :provider and uc.email = :email")
    public UserConnection getUserConnection(@Param("provider") ProviderType provider, @Param("email") String email);

    @Query(value = "select uc from UserConnection uc where uc.user.id = :id and uc.provider = :provider")
    public UserConnection getUserConnection(@Param("id") Long id, @Param("provider") ProviderType provider);

    @Modifying
    @Query("delete from UserConnection uc where uc.user.id = :id and uc.provider = :provider")
    public void deleteUserConnection(@Param("id") Long id, @Param("provider") ProviderType provider);

    @Query(value = "select uc from UserConnection uc where uc.provider = :provider and uc.providerId = :providerId")
    public UserConnection getUserConnectionByProviderId(@Param("provider") ProviderType provider,@Param("providerId") String providerId);

    @Query(value = "select uc from UserConnection uc where  uc.provider = :provider and uc.user.id = :userId")
    public UserConnection getUserConnectionByUserId(@Param("provider") ProviderType provider, @Param("userId") Long userId);
}
