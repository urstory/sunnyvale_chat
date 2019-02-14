package kr.co.sunnyvale.chat.repository;

import kr.co.sunnyvale.chat.domain.ExtraUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExtraUserRepository extends JpaRepository<ExtraUser, Long> {
    @Query(value = "select eu from ExtraUser eu where eu.email = :email")
    public ExtraUser getExtraUser(@Param("email") String email);

    @Query(value = "select eu from ExtraUser eu where eu.uuid = :uuid")
    public ExtraUser getExtraUserByUuid(@Param("uuid") String uuid);
}
