
# Using alternative email providers

Google App Engine provides JavaMail as a default email service, but the service is limited to 100 emails per day which is insufficient for TEAMMATES usage.
We need to resort to third-party email service to fulfill this need.

This document will line out the alternative email services configured for use in TEAMMATES.

- [SendGrid](#sendgrid)

## SendGrid

- Website: https://sendgrid.com
- Google App Engine reference: https://cloud.google.com/appengine/docs/flexible/java/sending-emails-with-sendgrid

To use SendGrid in TEAMMATES, create a free SendGrid account, create an API key, and update the relevant values in `build.properties`.
