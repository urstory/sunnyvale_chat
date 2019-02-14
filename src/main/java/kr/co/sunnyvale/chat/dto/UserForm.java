package kr.co.sunnyvale.chat.dto;

import lombok.Data;

@Data
public class UserForm {
    private String name;
    private String email;
    private String nickName;
    private String password;
}
