package kr.co.sunnyvale.chat.security.rememberme;

import kr.co.sunnyvale.chat.domain.PersistentLogins;
import kr.co.sunnyvale.chat.repository.PersistentLoginsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class TokenRepository implements PersistentTokenRepository {
    private final PersistentLoginsRepository persistentLoginsRepository;

    @Override
    @Transactional
    public void createNewToken(PersistentRememberMeToken token) {
        PersistentLogins persistentLogins = new PersistentLogins();
        persistentLogins.setUsername(token.getUsername());
        persistentLogins.setSeries(token.getSeries());
        persistentLogins.setToken(token.getTokenValue());
        persistentLogins.setLastUsed(token.getDate());
        persistentLoginsRepository.save(persistentLogins);
    }

    @Override
    @Transactional
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        PersistentLogins persistentLogins = persistentLoginsRepository.getPersistentLoginsBySeries(series);
        persistentLogins.setToken(tokenValue);
        persistentLogins.setLastUsed(lastUsed);
    }

    @Override
    @Transactional(readOnly = true)
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        try {
            PersistentLogins persistentLogins = persistentLoginsRepository.getPersistentLoginsBySeries(seriesId);
            if(persistentLogins == null)
                return null;
            PersistentRememberMeToken persistentRememberMeToken = new PersistentRememberMeToken(persistentLogins.getUsername(), persistentLogins.getSeries(), persistentLogins.getToken(), persistentLogins.getLastUsed());
            return persistentRememberMeToken;
        }catch (Exception e) {
        }
        return null;
    }

    @Override
    @Transactional
    public void removeUserTokens(String username) {
//        persistentLoginsRepository.deleteByUsername(username);
    }
}
