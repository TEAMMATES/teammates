package teammates.storage.sqlentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import teammates.common.util.FieldValidator;

/**
 * Binds an OIDC (issuer, subject) pair to an {@link Account}. Multiple rows per account
 * allow multiple login methods for the same person.
 */
@Entity
@Table(name = "AccountIdentities", uniqueConstraints = {
        @UniqueConstraint(name = "Unique issuer and subject", columnNames = { "issuer", "subject" })
})
public class AccountIdentity extends BaseEntity {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "accountId", nullable = false)
    private Account account;

    @Column(nullable = false, length = FieldValidator.OIDC_ISSUER_MAX_LENGTH)
    private String issuer;

    @Column(nullable = false, length = FieldValidator.OIDC_SUBJECT_MAX_LENGTH)
    private String subject;

    @Column(nullable = false, length = 254)
    private String loginIdentifier;

    protected AccountIdentity() {
        // Hibernate
    }

    public AccountIdentity(String issuer, String subject, String loginIdentifier) {
        this.setId(UUID.randomUUID());
        this.setIssuer(issuer);
        this.setSubject(subject);
        this.setLoginIdentifier(loginIdentifier);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getLoginIdentifier() {
        return loginIdentifier;
    }

    public void setLoginIdentifier(String loginIdentifier) {
        this.loginIdentifier = loginIdentifier;
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();
        addNonEmptyError(FieldValidator.getInvalidityInfoForOidcIssuer(issuer), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForOidcSubject(subject), errors);
        return errors;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (this == other) {
            return true;
        }
        if (getClass() != other.getClass()) {
            return false;
        }
        AccountIdentity that = (AccountIdentity) other;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
