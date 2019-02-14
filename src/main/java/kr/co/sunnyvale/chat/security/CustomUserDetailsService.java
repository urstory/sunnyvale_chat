package kr.co.sunnyvale.chat.security;

import kr.co.sunnyvale.chat.domain.User;
import kr.co.sunnyvale.chat.domain.Role;
import kr.co.sunnyvale.chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserService userService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User member = userService.getUser(email);
        if(member == null)
            throw new UsernameNotFoundException(email + " is not found");

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for(Role role : member.getRoles()){
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        }

        CustomUserDetails userDetails = new CustomUserDetails(email, member.getPassword(), authorities);
        userDetails.setName(member.getName());
        userDetails.setId(member.getId());
        userDetails.setNickName(member.getNickName());
        return userDetails;
    }
}
