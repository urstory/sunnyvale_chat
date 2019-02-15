package kr.co.sunnyvale.chat.config;

import kr.co.sunnyvale.chat.security.CustomUserDetailsService;
import kr.co.sunnyvale.chat.security.rememberme.TokenRepository;
import kr.co.sunnyvale.chat.security.social.facebook.FacebookOAuth2ConnectAuthenticationProcessingFilter;
import kr.co.sunnyvale.chat.security.CustomUserDetails;
import kr.co.sunnyvale.chat.security.social.SocialService;
import kr.co.sunnyvale.chat.security.social.facebook.FacebookOAuth2JoinAuthenticationProcessingFilter;
import kr.co.sunnyvale.chat.security.social.facebook.FacebookOAuth2LoginAuthenticationProcessingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

@EnableWebSecurity
@EnableOAuth2Client
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Qualifier("oauth2ClientContext")
    @Autowired
    private OAuth2ClientContext oauth2ClientContext;
    @Autowired
    private SocialService socialService;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    CustomUserDetailsService customUserDetailsService;

    /*
    인증에 대한 처리를 아예 무시할 경로를 설정.
    ex> http://localhost:8080/logo.gif
    AntPathRequestMatcher : ant 문법으로 path를 지정. ant :빌드도구
    /css/** , /js/**, /images/**, /webjars/**, ** /favicon.ico
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers(new AntPathRequestMatcher("/**.html"))
                .requestMatchers(new AntPathRequestMatcher("/static/**"))
                .requestMatchers(new AntPathRequestMatcher("/webjars/**"))
                .requestMatchers(new AntPathRequestMatcher("/node_modules/**"))
                .requestMatchers(new AntPathRequestMatcher("/assets/**"));
    }

    /*
    http://localhost:8080/logout - 로그아웃처리
    http://localhost:8080/ - 모두 접근가능
    http://localhost:8080/admin/** - admin권한 사용자만 접근 가능.
    http://localhost:8080/users/login - 아무나 접근할 수 있다.
    http://localhost:8080/admin/** - member권한 사용자만 접근 가능

    GET http://localhost:8080/users/login - 로그인 화면
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .permitAll().and()
                .authorizeRequests()
                .antMatchers("/errors/**").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/hasUserConnection/**").permitAll()
                .antMatchers("/login/**").permitAll()
                .antMatchers("/join/**").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/users/login").permitAll()
                .antMatchers("/users/findPassword").permitAll()
                .antMatchers("/users/findPasswordResult").permitAll()
                .antMatchers("/users/changePasswordForForgetPasswordUser").permitAll()
                .antMatchers("/users/changePasswordForForgetPasswordUserResult").permitAll()
                .antMatchers("/users/joinform").permitAll()
                .antMatchers("/api/users/join").permitAll()
                .antMatchers("/api/users/existEmail").permitAll()
                .antMatchers("/api/users/existNickName").permitAll()
                .antMatchers("/users/socialjoin").hasRole("TEMPUSER")
                .antMatchers("/users/extrauser").permitAll()
                .antMatchers("/users/acceptJoinUser").permitAll()
                .antMatchers("/users/acceptJoinUserResult").permitAll()
                .antMatchers("/users/**").hasRole("USER")
                .antMatchers("/api/**").hasRole("USER")
                .anyRequest().fullyAuthenticated().and()
                .formLogin()
                .loginPage("/users/login")
                .usernameParameter("id").passwordParameter("password")
                .loginProcessingUrl("/users/login")
                .failureUrl("/users/login").and()
                .rememberMe().rememberMeParameter("remember-me").key("uniqueAndSecret").tokenValiditySeconds(86400).rememberMeServices(rememberMeServices()).and()
                .exceptionHandling().accessDeniedHandler(handler403()).authenticationEntryPoint(delegatingEntryPoint()).and()
                .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
    }



    @Bean
    public RememberMeServices rememberMeServices() {

        PersistentTokenBasedRememberMeServices rememberMeServices =
                new PersistentTokenBasedRememberMeServices("uniqueAndSecret", customUserDetailsService, tokenRepository);

        rememberMeServices.setParameter("remember-me");
        return rememberMeServices;
    }



    @Bean
    public AuthenticationEntryPoint delegatingEntryPoint() {
        final LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> map = new LinkedHashMap();
        map.put(new AntPathRequestMatcher("/"), new LoginUrlAuthenticationEntryPoint("/users/login"));
        map.put(new AntPathRequestMatcher("/api/**"), new Http403ForbiddenEntryPoint());

        final DelegatingAuthenticationEntryPoint entryPoint = new DelegatingAuthenticationEntryPoint(map);
        entryPoint.setDefaultEntryPoint(new LoginUrlAuthenticationEntryPoint("/users/login"));

        return entryPoint;
    }

    private AccessDeniedHandler handler403(){
        return new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if(authentication != null){
                    CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
                    Collection<GrantedAuthority> authorities = customUserDetails.getAuthorities();
                    for(GrantedAuthority grantedAuthority : authorities){
                        if("ROLE_EXTRAUSER".equals(grantedAuthority.getAuthority())){
                            httpServletResponse.sendRedirect("/users/extrauser");
                            return;
                        }
                        if("ROLE_TEMPUSER".equals(grantedAuthority.getAuthority())){
                            httpServletResponse.sendRedirect("/users/socialjoin");
                            return;
                        }
                    }
                } // if
                httpServletResponse.sendRedirect("/errors/403");
            }
        };
    }

    private Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();
        ClientResources facebookClientResources = facebook();

        FacebookOAuth2LoginAuthenticationProcessingFilter facebookOAuth2LoginAuthenticationProcessingFilter =
                new FacebookOAuth2LoginAuthenticationProcessingFilter("/login/facebook", socialService);
        facebookOAuth2LoginAuthenticationProcessingFilter.setAuthenticationSuccessHandler(successHandler("/"));
        filters.add(ssoFilter(facebookClientResources, facebookOAuth2LoginAuthenticationProcessingFilter));

        FacebookOAuth2ConnectAuthenticationProcessingFilter facebookConnectOAuth2ConnectAuthenticationProcessingFilter =
                new FacebookOAuth2ConnectAuthenticationProcessingFilter("/connect/facebook", socialService);
        facebookConnectOAuth2ConnectAuthenticationProcessingFilter.setAuthenticationSuccessHandler(successHandler("/users/info"));
        filters.add(ssoFilter(facebookClientResources, facebookConnectOAuth2ConnectAuthenticationProcessingFilter));

        FacebookOAuth2JoinAuthenticationProcessingFilter facebookJoinOAuth2ClientAuthenticationProcessingFilter =
                new FacebookOAuth2JoinAuthenticationProcessingFilter("/join/facebook", socialService);
        facebookJoinOAuth2ClientAuthenticationProcessingFilter.setAuthenticationSuccessHandler(successHandler("/"));
        filters.add(ssoFilter(facebookClientResources, facebookJoinOAuth2ClientAuthenticationProcessingFilter));

        filter.setFilters(filters);
        return filter;
    }

    private AuthenticationSuccessHandler successHandler(String redirectUrl){
        final String url = redirectUrl;

        return new AuthenticationSuccessHandler() {
            private RequestCache requestCache = new HttpSessionRequestCache();
            private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

            @Override
            public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {

                String redirectUrl = url;
                SavedRequest savedRequest = requestCache.getRequest(httpServletRequest, httpServletResponse);
                if(savedRequest != null){
                    requestCache.removeRequest(httpServletRequest, httpServletResponse);
                    redirectUrl = savedRequest.getRedirectUrl();
                }
//
                boolean tempUserFlag = authentication.getAuthorities().stream().anyMatch(role ->{
                    if(((GrantedAuthority) role).getAuthority().equals("ROLE_TEMPUSER"))
                        return true;
                    else
                        return false;
                });
                if(tempUserFlag){
                    httpServletResponse.sendRedirect("/users/socialjoin");
                }else{
                    redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, redirectUrl);
                }
            }
        };
    }

    private Filter ssoFilter(ClientResources client, OAuth2ClientAuthenticationProcessingFilter filter) {
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
        filter.setRestTemplate(restTemplate);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(client.getResource().getUserInfoUri(), client.getClient().getClientId());
        filter.setTokenServices(tokenServices);
        tokenServices.setRestTemplate(restTemplate);
        return filter;
    }


    @Bean
    @ConfigurationProperties("facebook")
    public ClientResources facebook() {
        return new ClientResources();
    }

    /*
    인증 요청에 따른 리다이렉션을 위한 빈을 등록한다. 이때 setOrder 메서드로 Spring Security 필터 보다 우선순위를 낮게 설정한다.
     */
    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }
}


