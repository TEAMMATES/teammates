<frontmatter>
  title: "Emails"
</frontmatter>

# Using alternative email providers

In order for actual emails to be sent by the system, we need to use third-party email service.

This document will outline some of the email services that can be used in TEAMMATES.

In many cases, you have to register your domain name with the email service and configure your DNS provider so that the service can start sending email for you.
The details for this can be seen on the documentation of each service.

## SendGrid

- Website: https://sendgrid.com

To use SendGrid in TEAMMATES, create a free SendGrid account, create an API key, and update the relevant values in `build.properties`.

## Mailgun

- Website: https://www.mailgun.com

To use Mailgun in TEAMMATES, create a free Mailgun account, get a domain name and find its API Key, and update the relevant values in `build.properties`.

## Mailjet

- Website: https://www.mailjet.com

To use Mailjet in TEAMMATES, create a free Mailjet account, find your API key and secret key, and update the relevant values in `build.properties`.
