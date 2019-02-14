package kr.co.sunnyvale.chat.security.social;

public class OtherUserEmailException extends RuntimeException{
    private Long socialUserId;
    private Long loginUserId;

    public OtherUserEmailException(Long socialUserId, long loginUserId, String msg){
        super(msg);
    }

    public OtherUserEmailException(Long socialUserId, long loginUserId, Exception ex){
        super(ex);
    }


    public Long getSocialUserId() {
        return socialUserId;
    }

    public Long getLoginUserId() {
        return loginUserId;
    }
}
