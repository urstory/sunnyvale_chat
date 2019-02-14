package kr.co.sunnyvale.chat.security.social.facebook;

import kr.co.sunnyvale.chat.domain.ProviderType;
import kr.co.sunnyvale.chat.domain.UserConnection;
import lombok.Getter;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

@Getter
public class FacebookUserDetails {

    private String id;
    private String name;
    private String email;
    private long expiration;
    private String access_token;

    public void setAccessToken(OAuth2AccessToken accessToken) {
        this.access_token = accessToken.getValue();
        this.expiration = accessToken.getExpiration().getTime();
    }

    public UserConnection createUserConnection(){
        return UserConnection.builder()
                .expireTime(this.getExpiration())
                .accessToken(this.getAccess_token())
                .providerId(this.getId())
                .email(this.getEmail())
                .displayName(this.getName())
                .provider(ProviderType.FACEBOOK)
                .build();
    }
}