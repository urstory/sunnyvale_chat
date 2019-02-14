package kr.co.sunnyvale.chat.security.social;

public class AlreadyConnectOtherSocialIdException extends RuntimeException{
    public AlreadyConnectOtherSocialIdException(String msg){
        super(msg);
    }

    public AlreadyConnectOtherSocialIdException(Exception ex){
        super(ex);
    }
}
