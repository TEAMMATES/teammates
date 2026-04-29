# Cron Job Setup Guide

TEAMMATES cron endpoints must be triggered by an external scheduler (e.g. Google Cloud Scheduler).

## Authentication

All cron requests must include:

```
Authorization: Bearer <app.cron.and.worker.secret>
```

Configure this header in your scheduler. The secret is set in `build.properties` as `app.cron.and.worker.secret`.

## Timezone

All schedules below use **UTC**. Configure your scheduler to use this timezone.

## Format 1: Human-Readable Schedule

| Endpoint                                    | Description                                     | Schedule (UTC)        |
| ------------------------------------------- | ----------------------------------------------- | --------------------- |
| `/auto/feedbackSessionOpenedReminders`      | Emails for sessions that just opened            | Every hour at :02     |
| `/auto/feedbackSessionOpeningSoonReminders` | Emails for sessions opening in 24h              | Every hour at :11     |
| `/auto/feedbackSessionClosingSoonReminders` | Reminders for sessions closing in 24h           | Every hour at :06     |
| `/auto/feedbackSessionClosedReminders`      | Emails for sessions that just closed            | Every hour at :08     |
| `/auto/feedbackSessionPublishedReminders`   | Emails for sessions just published              | Every hour at :04     |
| `/auto/calculateUsageStatistics`            | Gather usage statistics                         | Every hour at :01     |
| `/auto/compileLogs`                         | Compile severe logs, send email                 | Every 5 minutes       |
| `/auto/cleanupFeedbackSessionLogs`          | Delete student activity logs older than 90 days | Every Sunday at 03:00 |

## Format 2: Copy-Paste (Unix Cron)

Use these expressions with your scheduler of choice (e.g. Google Cloud Scheduler). Build the full URL as `https://your-teammates-app.com/auto/<path>`.

```
# Timezone: UTC (set in your scheduler)
# Format: cron_expression | url_path

2 * * * *     | /auto/feedbackSessionOpenedReminders
11 * * * *    | /auto/feedbackSessionOpeningSoonReminders
6 * * * *     | /auto/feedbackSessionClosingSoonReminders
8 * * * *     | /auto/feedbackSessionClosedReminders
4 * * * *     | /auto/feedbackSessionPublishedReminders
1 * * * *     | /auto/calculateUsageStatistics
*/5 * * * *   | /auto/compileLogs
0 3 * * 0     | /auto/cleanupFeedbackSessionLogs
```

## Example: Google Cloud Scheduler

1. Create a job with HTTP target.
2. URL: `https://your-teammates-app.com/auto/feedbackSessionOpenedReminders`
3. Add header: `Authorization: Bearer <your-secret>`
4. Schedule: `2 * * * *` (Unix cron format)
5. Timezone: UTC.
