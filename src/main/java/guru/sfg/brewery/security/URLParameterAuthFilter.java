package guru.sfg.brewery.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.util.matcher.RequestMatcher;
import javax.servlet.http.HttpServletRequest;

@Slf4j
public class URLParameterAuthFilter extends SfgRestAbstractFilter {

    public URLParameterAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    protected String getPassword(HttpServletRequest httpServletRequest) {
        log.debug("URL AUTH FILTER: Api Secret: " +  httpServletRequest.getParameter("Api-Secret"));
        return httpServletRequest.getParameter("Api-Secret");
    }

    @Override
    protected String getUserName(HttpServletRequest httpServletRequest) {
        log.debug("URL AUTH FILTER: Api Key: " +  httpServletRequest.getParameter("Api-Key"));
        return httpServletRequest.getParameter("Api-Key");
    }

}