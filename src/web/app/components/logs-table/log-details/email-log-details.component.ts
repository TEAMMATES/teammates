import { Component, Input } from '@angular/core';
import { EmailSentLogDetails, GeneralLogEntry, LogEvent } from '../../../../types/api-output';

/**
 * Additional details for email sent logs.
 */
@Component({
  selector: 'tm-email-log-details',
  templateUrl: './email-log-details.component.html',
  styleUrls: ['./email-log-details.component.scss', './log-details.component.scss'],
})
export class EmailLogDetailsComponent {

  logValue!: GeneralLogEntry;
  details!: EmailSentLogDetails;
  emailContent?: string;

  @Input()
  get log(): GeneralLogEntry {
    return this.logValue;
  }

  set log(log: GeneralLogEntry) {
    this.logValue = log;
    if (log.details && log.details.event === LogEvent.EMAIL_SENT) {
      const details: EmailSentLogDetails = JSON.parse(JSON.stringify(log.details)) as EmailSentLogDetails;
      this.emailContent = details.emailContent;
      details.emailContent = undefined;
      this.details = details;
    }
  }

}
