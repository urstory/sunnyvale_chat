package kr.co.sunnyvale.chat.security;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        AuthUser authUser =
                methodParameter.getParameterAnnotation(AuthUser.class);
        if(authUser != null
                && methodParameter.getParameterType() ==  CustomUserDetails.class){
            return true;
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        // SecurityContextHolder를 이용하여 결과를 얻어서 리턴.
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        Object p = authentication.getPrincipal();
        if(p == null || p.getClass() == String.class){
            return null;
        }
        return p;
    }
}
