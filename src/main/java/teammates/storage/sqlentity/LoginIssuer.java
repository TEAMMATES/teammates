package teammates.storage.sqlentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import teammates.common.datatransfer.OidcProviderNameType;
import teammates.common.util.FieldValidator;

/**
 * Maps an OIDC issuer URL to a human-readable provider name.
 */
@Entity
@Table(name = "LoginIssuers")
public class LoginIssuer extends BaseEntity {

    @Id
    @Column(nullable = false)
    private String issuer;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OidcProviderNameType providerName;

    protected LoginIssuer() {
        // required by Hibernate
    }

    public LoginIssuer(String issuerString, OidcProviderNameType providerName) {
        this.setIssuer(issuerString);
        this.setProviderName(providerName);
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public OidcProviderNameType getProviderName() {
        return providerName;
    }

    public void setProviderName(OidcProviderNameType providerName) {
        this.providerName = providerName;
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForOidcIssuer(issuer), errors);
        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField(
                "provider name", providerName), errors);

        return errors;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            LoginIssuer that = (LoginIssuer) other;
            return Objects.equals(this.issuer, that.issuer)
                    && Objects.equals(this.providerName, that.providerName);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(issuer, providerName);
    }

    @Override
    public String toString() {
        return "LoginIssuer [issuer=" + issuer + ", providerName=" + providerName + "]";
    }
}
