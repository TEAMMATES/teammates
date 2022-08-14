package teammates.storage.entity;

import java.security.SecureRandom;
import java.time.Instant;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Translate;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.util.StringHelper;

/**
 * Represents an AccountRequest entity.
 */
@Entity
@Index
public class AccountRequest extends BaseEntity {

    @Id
    private String id;

    private String registrationKey;

    private String name;

    private String institute;

    private String email;

    private String homePageUrl;

    private String comments;

    private AccountRequestStatus status;

    @Translate(InstantTranslatorFactory.class)
    private Instant createdAt;

    /**
     * The time when this account request is last processed by an administrator.
     * Processing includes edit, approve, reject, and reset. Initially, lastProcessedAt is set to {@code null}.
     */
    @Translate(InstantTranslatorFactory.class)
    private Instant lastProcessedAt;

    @Translate(InstantTranslatorFactory.class)
    private Instant registeredAt;

    @SuppressWarnings("unused")
    private AccountRequest() {
        // required by Objectify
    }

    public AccountRequest(String name, String institute, String email, String homePageUrl, String comments) {
        this.setName(name);
        this.setInstitute(institute);
        this.setEmail(email);
        this.setHomePageUrl(homePageUrl);
        this.setComments(comments);
        this.setId(generateId(email, institute));
        this.setRegistrationKey(generateRegistrationKey());
        this.setStatus(AccountRequestStatus.SUBMITTED);
        this.setCreatedAt(Instant.now());
        this.setLastProcessedAt(null);
        this.setRegisteredAt(null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id.trim();
    }

    public String getRegistrationKey() {
        return registrationKey;
    }

    public void setRegistrationKey(String registrationKey) {
        this.registrationKey = registrationKey.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.trim();
    }

    public String getHomePageUrl() {
        return homePageUrl;
    }

    public void setHomePageUrl(String homePageUrl) {
        this.homePageUrl = homePageUrl.trim();
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments.trim();
    }

    public AccountRequestStatus getStatus() {
        return status;
    }

    public void setStatus(AccountRequestStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastProcessedAt() {
        return lastProcessedAt;
    }

    public void setLastProcessedAt(Instant lastProcessedAt) {
        this.lastProcessedAt = lastProcessedAt;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Instant registeredAt) {
        this.registeredAt = registeredAt;
    }

    /**
     * Generates an unique ID for the account request.
     */
    public static String generateId(String email, String institute) {
        // Format: email%institute e.g., adam@u.nus.edu%National University of Singapore, Singapore
        return email + '%' + institute;
    }

    /**
     * Generate unique registration key for the account request.
     * The key contains random elements to avoid being guessed.
     */
    private String generateRegistrationKey() {
        String uniqueId = getId();
        SecureRandom prng = new SecureRandom();

        return StringHelper.encrypt(uniqueId + prng.nextInt());
    }
}
