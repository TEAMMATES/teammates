package teammates.common.datatransfer;

import teammates.common.util.StringHelper;

public enum OidcProviderNameType {
    GOOGLE("Google"),
    MS_ENTRA("MSEntra");

    private final String providerName;

    OidcProviderNameType(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderName() {
        return providerName;
    }

    public String toString() {
        return this.providerName;
    }

    public static OidcProviderNameType fromProviderName(String providerName) {
        if (StringHelper.isEmpty(providerName)) {
            return null;
        }

        for (OidcProviderNameType provider : OidcProviderNameType.values()) {
            if (provider.getProviderName().equals(providerName)) {
                return provider;
            }
        }

        return null;
    }
}
