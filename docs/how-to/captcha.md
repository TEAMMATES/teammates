<frontmatter>
  title: "CAPTCHA"
</frontmatter>

# CAPTCHA

TEAMMATES uses [reCAPTCHA](https://developers.google.com/recaptcha/) on the Recover Session Links page. By default, the CAPTCHA widget is disabled since no keys are configured — this is fine for development but should be enabled for production.

## Setup

1. Register for reCAPTCHA at [google.com/recaptcha/admin](https://www.google.com/recaptcha/admin).
1. If using CAPTCHA in development, add `localhost` and/or `127.0.0.1` to the allowed domains.
1. Copy the keys to the config files:
   - Site key → `captchaSiteKey` in `config.ts`
   - Secret key → `app.captcha.secretkey` in `build.properties`
