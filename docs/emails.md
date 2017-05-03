
# Using alternative email providers

Google App Engine provides JavaMail (reference: https://cloud.google.com/appengine/docs/java/mail/) as a default email service, but the service is limited to 100 emails per day which is insufficient for TEAMMATES usage.
We need to resort to third-party email service to fulfill this need.

This document will outline the alternative email services configured for use in TEAMMATES.

- [SendGrid](#sendgrid)
- [Mailgun](#mailgun)
- [Mailjet](#mailjet)

In many cases, you have to register your domain name with the email service and configure your DNS provider so that the service can start sending email for you.
The details for this can be seen on the documentation of each service.

## SendGrid

- Website: https://sendgrid.com
- Google App Engine reference: https://cloud.google.com/appengine/docs/flexible/java/sending-emails-with-sendgrid

To use SendGrid in TEAMMATES, create a free SendGrid account, create an API key, and update the relevant values in `build.properties`.

## Mailgun

- Website: https://www.mailgun.com
- Google App Engine reference: https://cloud.google.com/appengine/docs/java/mail/mailgun

To use Mailgun in TEAMMATES, create a free Mailgun account, get a domain name and find its API Key, and update the relevant values in `build.properties`.

## Mailjet

- Website: https://www.mailjet.com
- Google App Engine reference: https://cloud.google.com/appengine/docs/java/mail/mailjet

To use Mailjet in TEAMMATES, create a free Mailjet account, find your API key and secret key, and update the relevant values in `build.properties`.
