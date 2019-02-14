package kr.co.sunnyvale.chat.controller;

import kr.co.sunnyvale.chat.domain.ExtraUser;
import kr.co.sunnyvale.chat.domain.ProviderType;
import kr.co.sunnyvale.chat.domain.User;
import kr.co.sunnyvale.chat.security.AuthUser;
import kr.co.sunnyvale.chat.security.CustomUserDetails;
import kr.co.sunnyvale.chat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/joinform")
    public String joinform(){
        return "joinform";
    }

    @GetMapping("/alreadyJoin")
    public String alreadyJoin(){
        return "alreadyJoin";
    }

    @GetMapping("/info")
    public String info(Principal principal, ModelMap modelMap){
        CustomUserDetails loginUser =
                (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean b = userService.hasUserConnection(loginUser.getId(), ProviderType.FACEBOOK);
        modelMap.addAttribute("loginUser", loginUser);
        modelMap.addAttribute("hasUserConnection", b);
        return "info";
    }

    @GetMapping("/socialjoin")
    public String socialjoin(Principal principal, ModelMap modelMap){
        CustomUserDetails loginUser =
                (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        modelMap.addAttribute("loginUser", loginUser);
        return "socialjoin";
    }

    @PostMapping("/socialjoin")
    public String postSocialjoin(
            @RequestParam(name = "email")String email,
            @RequestParam(name = "name")String name,
            @AuthUser CustomUserDetails loginUser, ModelMap modelMap){

        User user = userService.changeUserRole(loginUser.getId(), loginUser.getEmail(), email, name, "EXTRAUSER");

        String uuid = UUID.randomUUID().toString();
        ExtraUser extraUser = new ExtraUser();
        extraUser.setEmail(email);
        extraUser.setUuid(uuid);
        extraUser.setRegdate(LocalDateTime.now());
        userService.saveExtraUser(extraUser);

        // USER권한을 가지도록 변경함.
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_EXTRAUSER"));
        CustomUserDetails customUserDetails = new CustomUserDetails(user.getEmail(), "", authorities);
        customUserDetails.setId(user.getId());
        customUserDetails.setNickName(user.getNickName());
        customUserDetails.setName(user.getName());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                customUserDetails, null, authorities)
        );

        return "redirect:/users/extrauser";
    }

    @GetMapping("/findPassword")
    public String findPasswordForm(
            @RequestParam(name = "error", required = false) String error,
            HttpSession session, ModelMap modelMap){
        int code = (int)(Math.random() * 9000) + 1000;
        session.setAttribute("code",code + "");
        modelMap.addAttribute("code", code + "");
        if(error != null)
            modelMap.addAttribute("error", "코드값을 알맞게 입력해주세요.");
        return "findPasswordForm";
    }

    @PostMapping("/findPassword")
    public String findPassword(
            @RequestParam("id") String id,
            @RequestParam("code") String code,
            HttpSession session
    ){
        String sessionCode = (String)session.getAttribute("code");
        if(sessionCode == null){
            return "redirect:/users/findPassword?error=codeerror";
        }
        if(!code.equals(sessionCode)){
            return "redirect:/users/findPassword?error=codeerror";
        }

        User user = userService.getUser(id);
        if(user == null)
            return "redirect:/users/findPassword?error=codeerror";

        String uuid = UUID.randomUUID().toString();

        return "redirect:/users/findPasswordResult";
    }

    @GetMapping("/findPasswordResult")
    public String findPasswordResult(){
        return "findPassword";
    }


    @GetMapping("/changePasswordForForgetPasswordUserResult")
    public String changePasswordForForgetPasswordUserResult(){
        return "changePasswordForForgetPasswordUserResult";
    }

    @GetMapping("/extrauser")
    public String extrauser(){
        return "extrauser";
    }

    @GetMapping("/acceptJoinUser")
    public String acceptJoinUser(
            @RequestParam("email") String email,
            @RequestParam("uuid") String uuid,
            HttpSession session
    ){
        ExtraUser extraUser = userService.getExtraUser(uuid);
        if(extraUser == null)
            throw new RuntimeException(email + "에 대한 인증확인을 잘못된 방법으로 요청했습니다.");
        if(!extraUser.getUuid().equals(uuid)){
            throw new RuntimeException(email + "에 대한 인증확인을 잘못된 방법으로 요청했습니다.");
        }
        if(extraUser.getEmail().equals(email)) {
            User user = userService.getUser(email);
            userService.changeUserRole(user.getId(), "USER");
            userService.deleteExtraUser(uuid);
        }
        return "redirect:/users/acceptJoinUserResult";
    }

    @GetMapping("/acceptJoinUserResult")
    public String acceptJoinUserResult(){
        return "acceptJoinUserResult";
    }
}
