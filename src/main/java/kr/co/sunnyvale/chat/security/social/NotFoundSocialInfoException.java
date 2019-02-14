package kr.co.sunnyvale.chat.security.social;

public class NotFoundSocialInfoException extends RuntimeException{
    public NotFoundSocialInfoException(String msg){
        super(msg);
    }

    public NotFoundSocialInfoException(Exception ex){
        super(ex);
    }
}
