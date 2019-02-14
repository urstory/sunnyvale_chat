package kr.co.sunnyvale.chat.security.social;

public class OtherSocialUserException extends RuntimeException{
    private Long socialUserId;
    private Long loginUserId;

    public OtherSocialUserException(Long socialUserId, long loginUserId, String msg){
        super(msg);
    }

    public OtherSocialUserException(Long socialUserId, long loginUserId, Exception ex){
        super(ex);
    }


    public Long getSocialUserId() {
        return socialUserId;
    }

    public Long getLoginUserId() {
        return loginUserId;
    }
}
