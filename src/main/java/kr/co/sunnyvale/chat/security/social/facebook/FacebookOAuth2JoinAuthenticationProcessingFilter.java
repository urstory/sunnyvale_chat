package kr.co.sunnyvale.chat.security.social.facebook;



import kr.co.sunnyvale.chat.domain.ProviderType;
import kr.co.sunnyvale.chat.domain.UserConnection;
import kr.co.sunnyvale.chat.security.social.*;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FacebookOAuth2JoinAuthenticationProcessingFilter extends OAuth2ClientAuthenticationProcessingFilter {

    private ObjectMapper mapper = new ObjectMapper();
    private SocialService socialService;

    public FacebookOAuth2JoinAuthenticationProcessingFilter(String processUrl, SocialService socialService) {
        super(processUrl);
        this.socialService = socialService;
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null)
            response.sendRedirect("/errors/alreadyLogin");

        final OAuth2AccessToken accessToken = restTemplate.getAccessToken();
        final OAuth2Authentication auth = (OAuth2Authentication) authResult;
        final Object details = auth.getUserAuthentication().getDetails();

        final FacebookUserDetails userDetails = mapper.convertValue(details, FacebookUserDetails.class);
        userDetails.setAccessToken(accessToken);

        final UserConnection userConnection = userDetails.createUserConnection();
        try {
            final UsernamePasswordAuthenticationToken authenticationToken = socialService.doJoin(ProviderType.FACEBOOK, userConnection);

            super.successfulAuthentication(request, response, chain, authenticationToken);
        }catch(AlreadyConnectOtherSocialIdException acose){
            response.sendRedirect("/errors/alreadyConnectOtherSocialId");
        }catch (OtherUserEmailException ouee) {
            response.sendRedirect("/errors/otherUserEmail");
        }catch(EmailNotFoundException enfe){
            response.sendRedirect("/errors/emailNotFound");
        }catch(OtherSocialUserException osue){
            response.sendRedirect("/errors/otherSocialUser");
        }catch(AlreadyJoinException aje){
            response.sendRedirect("/errors/alreadyJoin");
        }
    }

}