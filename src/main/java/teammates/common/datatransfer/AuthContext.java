package teammates.common.datatransfer;

import jakarta.annotation.Nullable;
import teammates.storage.entity.Account;
import teammates.storage.entity.User;
import teammates.ui.webapi.AuthType;

/**
 * Represents the authentication context of a user.
 * 
 * <p>
 * This class encapsulates all information needed to determine the
 * authentication status of a user.
 *
 * @param authType     The authentication type.
 * @param account      The user's account. If masquerading, this is the account
 *                     of the user being masqueraded as.
 * @param regKeyUser   The user associated with the registration key.
 * @param isAdmin      Indicates whether the user has admin privilege.
 * @param isMaintainer Indicates whether the user has maintainer privilege.
 */
public record AuthContext(
        AuthType authType,
        @Nullable Account account,
        @Nullable User regKeyUser,
        boolean isAdmin,
        boolean isMaintainer) {
}
