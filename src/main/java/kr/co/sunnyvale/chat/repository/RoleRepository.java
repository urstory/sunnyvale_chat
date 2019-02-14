package kr.co.sunnyvale.chat.repository;

import kr.co.sunnyvale.chat.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    public Role findRoleByName(String name);

}
