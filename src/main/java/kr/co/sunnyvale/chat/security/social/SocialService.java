package kr.co.sunnyvale.chat.security.social;

import kr.co.sunnyvale.chat.domain.ProviderType;
import kr.co.sunnyvale.chat.domain.Role;
import kr.co.sunnyvale.chat.domain.User;
import kr.co.sunnyvale.chat.domain.UserConnection;
import kr.co.sunnyvale.chat.security.CustomUserDetails;
import kr.co.sunnyvale.chat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SocialService {
    private final UserService userService;

    @Transactional
    public UsernamePasswordAuthenticationToken doLogin(ProviderType provider, UserConnection userConnection) {

        UserConnection alreadyUserConnection = null;

        if(userConnection.getEmail() != null) {
            alreadyUserConnection = userService.getUserConnection(provider, userConnection.getEmail());
            if (alreadyUserConnection == null) { // email로 찾을 수 없을 경우
                alreadyUserConnection = userService.getUserConnectionByProviderId(provider, userConnection.getProviderId());
            }
        }else
            alreadyUserConnection = userService.getUserConnectionByProviderId(provider, userConnection.getProviderId());

        // 이미 소셜 가입한 회원 (로그인 처리)
        if (alreadyUserConnection != null) {
            final User user = alreadyUserConnection.getUser();
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) { // 이미 로그인 된 상태
                CustomUserDetails loginUser = (CustomUserDetails) authentication.getPrincipal();
                if (loginUser.getId() != user.getId()) { // 로그인 한 사용자와 Social 로그인 한 사용자의 id가 같지 않을 경우
                    throw new OtherSocialUserException(user.getId(), loginUser.getId(), "다른 회원의 소셜 계정으로 로그인하였습니다.");
                }
            }
            return setAuthenticationToken(user);
        }
        throw new NotFoundSocialInfoException("로그인한 소셜 정보로 가입한 정보를 찾을 수 없습니다.");
    }

    @Transactional
    public UsernamePasswordAuthenticationToken connect(ProviderType provider, UserConnection userConnection) {
        // 소셜 연결은 반드시 로그인 되어 있어야 한다.
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails loginUser = (CustomUserDetails) authentication.getPrincipal();

        UserConnection alreadyUserConnection = null;
        if (userConnection.getEmail() != null) {
            alreadyUserConnection = userService.getUserConnection(provider, userConnection.getEmail());
            if(alreadyUserConnection == null) { // email로 찾을 수 없을 경우
                alreadyUserConnection = userService.getUserConnectionByProviderId(provider, userConnection.getProviderId());
            }
        }else {
            // email이 없을 경우에는 loginUser의 id로 구한다.
            alreadyUserConnection = userService.getUserConnection(loginUser.getId(), provider);
        }
        // 예전에 연결한 소셜계정과 다른 소셜계정으로 연결을 시도함
        if (alreadyUserConnection != null && !alreadyUserConnection.getProviderId().equals(userConnection.getProviderId())) {
            throw new AlreadyConnectOtherSocialIdException("이미 다른 페이스북 계정으로 연결을 하였습니다.");
        }

        // 이미 소셜 가입한 회원 (로그인 처리)
        if (alreadyUserConnection != null) {
            final User user = alreadyUserConnection.getUser();
            if (loginUser.getId() != user.getId()) { // 로그인 한 사용자와 Social 로그인 한 사용자의 id가 같지 않을 경우
                throw new OtherSocialUserException(user.getId(), loginUser.getId(), "다른 회원의 소셜 계정으로 로그인하였습니다.");
            }
            return setAuthenticationToken(user);
        } else { // 이미 소셜로 가입하지 않은 회원 (소셜 정보가 없을 경우)
            // UserConnection에서 이메일 주소가 없을 수 있기 때문에 loginId를 이용해서 User정보를 구한다.
            User alreadyUser = userService.addUserConnection(loginUser.getId(), userConnection);

            return setAuthenticationToken(alreadyUser);
        }
    }


    @Transactional
    public UsernamePasswordAuthenticationToken doJoin(ProviderType provider, UserConnection userConnection) {
        UserConnection alreadyUserConnection = null;
        if (userConnection.getEmail() != null) {
            alreadyUserConnection = userService.getUserConnection(provider, userConnection.getEmail());
            if(alreadyUserConnection == null) { // email로 찾을 수 없을 경우
                alreadyUserConnection = userService.getUserConnectionByProviderId(provider, userConnection.getProviderId());
            }
        }else {
            alreadyUserConnection = userService.getUserConnectionByProviderId(provider, userConnection.getProviderId());
        }

        // 소셜 가입을 하고자 했지만, 이미 가입되어 있다.
        if(alreadyUserConnection != null){
            throw new AlreadyJoinException("이미 가입한 소셜 계정입니다.");
        }

        if(userConnection.getEmail() != null) {
            User alreadyUser = userService.getUser(userConnection.getEmail());
            if (alreadyUser != null)
                throw new AlreadyJoinException("이미 가입한 계정입니다.");
        }

        final User user = userService.signUpSocial(userConnection);
        return setAuthenticationTempToken(user);
    }


    private UsernamePasswordAuthenticationToken setAuthenticationToken(User user) {
        Set<Role> roles = user.getRoles();
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        }
        CustomUserDetails customUserDetails = new CustomUserDetails(user.getEmail(), "", authorities);
        customUserDetails.setId(user.getId());
        customUserDetails.setNickName(user.getNickName());
        customUserDetails.setName(user.getName());
        return new UsernamePasswordAuthenticationToken(customUserDetails, null, authorities);
    }

    private UsernamePasswordAuthenticationToken setAuthenticationTempToken(User user) {
        Collection<? extends GrantedAuthority> authorities = getAuthorities("ROLE_TEMPUSER");
        CustomUserDetails customUserDetails = new CustomUserDetails(user.getEmail(), "", authorities);
        customUserDetails.setId(user.getId());
        customUserDetails.setNickName(user.getNickName());
        customUserDetails.setName(user.getName());
        return new UsernamePasswordAuthenticationToken(customUserDetails, null, authorities);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }
}
