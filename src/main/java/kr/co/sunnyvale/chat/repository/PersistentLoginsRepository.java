package kr.co.sunnyvale.chat.repository;

import kr.co.sunnyvale.chat.domain.PersistentLogins;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersistentLoginsRepository
        extends JpaRepository<PersistentLogins, String> {

    @Modifying
    @Query("delete from PersistentLogins pl where pl.username = ?1")
    public int deleteByUsername(String username);

    @Query("select pl from PersistentLogins pl where pl.series = :series")
    PersistentLogins getPersistentLoginsBySeries(@Param("series") String series);
}