# Setting up CAPTCHA

TEAMMATES uses [reCAPTCHA](https://developers.google.com/recaptcha/) on the `Recover Session Links` page as a security check. It helps protect the website by blocking spam bots and abusive traffic, and only allows valid human users to pass through.
By default, the CAPTCHA widget is not shown on the page since no keys are specified in the config files.
 
However, this should be changed for production environments:

1. Register for CAPTCHA [here](https://www.google.com/recaptcha/admin). You may be prompted to sign in with your Google account.
1. Include `localhost` and/or `127.0.0.1` in the CAPTCHA domains setting if you are using this for development as well.
1. After obtaining the CAPTCHA keys:
    - Copy the site key to `captchaSiteKey` in the `config.ts` file.
    - Copy the secret key to `app.captcha.secretkey` in the `build.properties` file.
