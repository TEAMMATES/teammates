package teammates.logic.core;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.email.MagicLinkEmailsLogic;
import teammates.logic.email.model.MagicLinkEmailContext;
import teammates.storage.api.MagicLinksDb;
import teammates.storage.entity.MagicLink;

/**
 * Handles operations related to magic links.
 *
 * @see MagicLink
 * @see MagicLinksDb
 */
public final class MagicLinksLogic {

    private static final MagicLinksLogic instance = new MagicLinksLogic();
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int TOKEN_BYTE_LENGTH = 32;

    private MagicLinksDb magicLinksDb;
    private MagicLinkEmailsLogic magicLinkEmailsLogic;

    private MagicLinksLogic() {
        // prevent initialization
    }

    public static MagicLinksLogic inst() {
        return instance;
    }

    void initLogicDependencies(MagicLinksDb magicLinksDb, MagicLinkEmailsLogic magicLinkEmailsLogic) {
        this.magicLinksDb = magicLinksDb;
        this.magicLinkEmailsLogic = magicLinkEmailsLogic;
    }

    /**
     * Creates or replaces a magic link for the given email address.
     *
     * @return the raw one-time token.
     * @throws InvalidParametersException if the magic link is not valid.
     */
    public String createMagicLink(String email) throws InvalidParametersException {
        Objects.requireNonNull(email);

        String token = generateToken();
        MagicLink magicLink = new MagicLink(email, hashToken(token), Instant.now());
        validateMagicLink(magicLink);

        magicLinksDb.persistMagicLink(magicLink);
        return token;
    }

    /**
     * Creates a magic link and enqueues the sign-in email for the given email address.
     */
    public void requestMagicLinkEmail(String email, String encryptedState) throws InvalidParametersException {
        Objects.requireNonNull(encryptedState);

        String token = createMagicLink(email);
        String magicLinkUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.EMAIL_LOGIN_CONFIRMATION_PAGE)
                .withParam(Const.ParamsNames.AUTH_STATE, encryptedState)
                .withParam(Const.ParamsNames.TOKEN, token)
                .toAbsoluteString();

        magicLinkEmailsLogic.enqueueMagicLinkEmail(new MagicLinkEmailContext(email, magicLinkUrl));
    }

    /**
     * Returns a magic link for the given raw token, or null if no matching link exists.
     */
    public MagicLink getMagicLinkByToken(String token) {
        Objects.requireNonNull(token);
        return magicLinksDb.getMagicLinkByTokenHash(hashToken(token));
    }

    /**
     * Consumes a usable magic link for the given raw token.
     *
     * <p>Successful consumption deletes the magic link to enforce one-time use.
     *
     * @return the consumed magic link.
     * @throws EntityDoesNotExistException if the token is unknown.
     * @throws InvalidParametersException if the token is not usable.
     */
    public MagicLink consumeMagicLink(String token) throws InvalidParametersException, EntityDoesNotExistException {
        Objects.requireNonNull(token);
        MagicLink magicLink = getMagicLinkByToken(token);
        if (magicLink == null) {
            throw new EntityDoesNotExistException("Magic link does not exist for the given token.");
        }

        if (!magicLink.isUsable(Instant.now())) {
            throw new InvalidParametersException("Invalid or expired magic link.");
        }

        magicLinksDb.deleteMagicLink(magicLink);
        return magicLink;
    }

    /**
     * Deletes a magic link.
     */
    public void deleteMagicLink(MagicLink magicLink) {
        Objects.requireNonNull(magicLink);
        magicLinksDb.deleteMagicLink(magicLink);
    }

    /**
     * Hashes a raw magic-link token for storage or lookup.
     */
    static String hashToken(String token) {
        Objects.requireNonNull(token);
        return StringHelper.generateSha256Hmac("magic-link:" + token);
    }

    private static String generateToken() {
        byte[] tokenBytes = new byte[TOKEN_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    private void validateMagicLink(MagicLink magicLink) throws InvalidParametersException {
        if (!magicLink.isValid()) {
            throw new InvalidParametersException(magicLink.getInvalidityInfo());
        }
    }

}
