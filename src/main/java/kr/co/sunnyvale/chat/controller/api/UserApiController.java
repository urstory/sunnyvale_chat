package kr.co.sunnyvale.chat.controller.api;

import kr.co.sunnyvale.chat.domain.ExtraUser;
import kr.co.sunnyvale.chat.domain.ProviderType;
import kr.co.sunnyvale.chat.domain.User;
import kr.co.sunnyvale.chat.dto.UserForm;
import kr.co.sunnyvale.chat.security.AuthUser;
import kr.co.sunnyvale.chat.security.CustomUserDetails;
import kr.co.sunnyvale.chat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController {
    private final UserService userService;

    @GetMapping("/info")
    public CustomUserDetails info(@AuthUser CustomUserDetails loginUser){
        return loginUser;
    }

    @GetMapping("/disconnectFacebook")
    public String deleteFacebookUserConnection(@AuthUser CustomUserDetails loginUser){
        User user = userService.getUser(loginUser.getId());
        if(user.getPassword() == null || "".equals(user.getPassword()))
            return "fail";
        userService.deleteUserConnection(loginUser.getId(), ProviderType.FACEBOOK);
        return "ok";
    }

    @GetMapping("/existEmail")
    public String existEmail(@AuthUser CustomUserDetails loginUser, @RequestParam(name = "email") String email){
        if(loginUser != null && loginUser.getEmail().equals(email)){
            return "false";
        }
        User user = userService.getUser(email);
        if(user == null)
            return "false";
        else
            return "true";
    }

    @GetMapping("/existNickName")
    public String existNickName(@RequestParam(name = "nickName") String nickName){
        User user = userService.getUserByNickName(nickName);
        if(user == null)
            return "false";
        else
            return "true";

    }

    @PutMapping("/changePassword")
    public String changePassword(@AuthUser CustomUserDetails loginUser, @RequestBody Map<String, String> map){
        String existingPassword = map.get("existingPassword");
        String password1 = map.get("password1");
        String password2 = map.get("password2");

        if(password1 == null || password2 == null || existingPassword == null)
            return "fail";

        if(password1.length() < 5 || password2.length() < 5)
            return "fail";

        if(!password1.equals(password2))
            return "fail";

        boolean flag = userService.changePassword(loginUser.getId(), existingPassword, password1);
        if(!flag)
            return "fail";

        return "ok";
    }

    @PutMapping("/changeNickName")
    public String changeNickName(@AuthUser CustomUserDetails loginUser, @RequestBody Map<String, String> map){
        Assert.hasText(map.get("nickName"), "별명을 입력하지 않았습니다.");
        userService.updateNickname(loginUser.getId(), map.get("nickName"));

        // 인증 정보에서 닉네임을 변경한다.
        CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        customUserDetails.setNickName(map.get("nickName"));
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        return "ok";
    }

    @PostMapping("/join")
    public User join(@RequestBody UserForm userForm){
        Assert.hasText(userForm.getName(), "이름을 입력하지 않았습니다.");
        Assert.hasText(userForm.getEmail(), "email을 입력하지 않았습니다.");
        Assert.hasText(userForm.getNickName(), "닉네임을 입력하지 않았습니다.");
        if(userForm.getName().length() < 2)
            throw new RuntimeException("이름은 2글자 이상 입력해야 합니다.");
        if(userForm.getNickName().length() < 1)
            throw new RuntimeException("별명은 1글자 이상 입력해야 합니다.");

        User user = new User();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setNickName(userForm.getNickName());

        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        user.setPassword(passwordEncoder.encode(userForm.getPassword()));

        // EXTRA 유저로 가입이 된다. email인증을 받아야 정식 회원이 된다.
        user = userService.addUser(user);

        String uuid = UUID.randomUUID().toString();
        ExtraUser extraUser = new ExtraUser();
        extraUser.setEmail(userForm.getEmail());
        extraUser.setUuid(uuid);
        extraUser.setRegdate(LocalDateTime.now());
        userService.saveExtraUser(extraUser);

        return user;
    }
}
