package kr.co.sunnyvale.chat.security.social;

public class AlreadyJoinException extends RuntimeException{
    public AlreadyJoinException(String msg){
        super(msg);
    }

    public AlreadyJoinException(Exception ex){
        super(ex);
    }
}
