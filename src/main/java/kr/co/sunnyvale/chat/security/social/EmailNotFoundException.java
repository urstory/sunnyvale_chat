package kr.co.sunnyvale.chat.security.social;

import kr.co.sunnyvale.chat.domain.ProviderType;

public class EmailNotFoundException extends RuntimeException {
    private String providerId;
    private ProviderType providerType;
    public EmailNotFoundException(String providerId, ProviderType provider, String msg){
        super(msg);

        this.providerId = providerId;
        this.providerType = provider;
    }

    public EmailNotFoundException(String providerId, ProviderType provider, Exception ex){
        super(ex);

        this.providerId = providerId;
        this.providerType = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public ProviderType getProviderType() {
        return providerType;
    }

}
