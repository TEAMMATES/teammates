package teammates.ui.servlets;

import java.util.Arrays;

import jakarta.servlet.http.HttpServletResponse;

import teammates.common.util.Config;

/**
 * Shared HTTP security headers used across servlet filters.
 */
final class SecurityHeaders {

    static final String STRICT_TRANSPORT_SECURITY = "max-age=31536000";
    static final String X_CONTENT_TYPE_OPTIONS = "nosniff";
    static final String REFERRER_POLICY = "strict-origin-when-cross-origin";
    static final String PERMISSIONS_POLICY =
            "accelerometer=(), camera=(), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), payment=(), usb=()";
    static final String X_FRAME_OPTIONS = "SAMEORIGIN";
    static final String X_XSS_PROTECTION = "1; mode=block";

    static final String IMG_SRC_CSP = Config.IS_DEV_SERVER
            ? "'self' data: http: https:"
            : "'self' data: https:";

    static final String CONTENT_SECURITY_POLICY = String.join("; ", Arrays.asList(
            "default-src 'none'",
            "script-src 'self' https://www.google.com/recaptcha/ https://www.gstatic.com/recaptcha/ "
                    + "https://cdn.jsdelivr.net/  https://apis.google.com/",
            "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net/ https://fonts.googleapis.com/",
            "frame-src 'self' docs.google.com https://www.google.com/recaptcha/ https://*.firebaseapp.com/",
            "img-src " + IMG_SRC_CSP,
            "font-src 'self' https://cdn.jsdelivr.net/ https://fonts.gstatic.com/",
            "connect-src 'self' https://*.googleapis.com/",
            "manifest-src 'self'",
            "form-action 'none'",
            "frame-ancestors 'self'",
            "base-uri 'self'"
    ));

    private SecurityHeaders() {
        // Utility class.
    }

    static void addCommonHeaders(HttpServletResponse response) {
        response.setHeader("Strict-Transport-Security", STRICT_TRANSPORT_SECURITY);
        response.setHeader("X-Content-Type-Options", X_CONTENT_TYPE_OPTIONS);
        response.setHeader("Referrer-Policy", REFERRER_POLICY);
        response.setHeader("Permissions-Policy", PERMISSIONS_POLICY);
    }

    static void addDocumentHeaders(HttpServletResponse response) {
        addCommonHeaders(response);
        response.setHeader("Content-Security-Policy", CONTENT_SECURITY_POLICY);
        response.setHeader("X-Frame-Options", X_FRAME_OPTIONS);
        response.setHeader("X-XSS-Protection", X_XSS_PROTECTION);
    }

}
