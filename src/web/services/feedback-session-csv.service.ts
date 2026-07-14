import { Injectable } from '@angular/core';
import { CsvHelper } from './csv-helper';

export interface FeedbackSessionCsvData {
  courseId: string;
  courseName: string;
  feedbackSessionName: string;
  deadline: string;
  coOwnerEmails?: string;
  supportEmail?: string;
}

export interface FeedbackSessionSubmissionCsvData extends FeedbackSessionCsvData {
  sessionInstructions: string;
  recipients: FeedbackSessionSubmissionCsvRecipient[];
}

export interface FeedbackSessionResultCsvData extends FeedbackSessionCsvData {
  recipients: FeedbackSessionResultCsvRecipient[];
}

export interface FeedbackSessionSubmissionCsvRecipient {
  email: string;
  name: string;
  submissionLink: string;
  deadline?: string;
}

export interface FeedbackSessionResultCsvRecipient {
  email: string;
  name: string;
  resultLink: string;
  deadline?: string;
}

/**
 * Service to generate CSV data for feedback session emails.
 */
@Injectable({
  providedIn: 'root',
})
export class FeedbackSessionCsvService {
  getCsvForSubmissionLinks(data: FeedbackSessionSubmissionCsvData): string {
    const csvRows: string[][] = [
      [
        'Email',
        'Name',
        'Submission Link',
        'Course Name',
        'Course ID',
        'Feedback Session Name',
        'Deadline',
        'Session Instructions',
        'Co-owner Emails',
        'Support Email',
      ],
      ...data.recipients.map((recipient: FeedbackSessionSubmissionCsvRecipient) => [
        recipient.email,
        recipient.name,
        recipient.submissionLink,
        data.courseName,
        data.courseId,
        data.feedbackSessionName,
        recipient.deadline ?? data.deadline,
        data.sessionInstructions,
        data.coOwnerEmails ?? '',
        data.supportEmail ?? '',
      ]),
    ];

    return CsvHelper.convertCsvContentsToCsvString(csvRows);
  }

  getCsvForResultLinks(data: FeedbackSessionResultCsvData): string {
    const csvRows: string[][] = [
      [
        'Email',
        'Name',
        'Result Link',
        'Course Name',
        'Course ID',
        'Feedback Session Name',
        'Deadline',
        'Co-owner Emails',
        'Support Email',
      ],
      ...data.recipients.map((recipient: FeedbackSessionResultCsvRecipient) => [
        recipient.email,
        recipient.name,
        recipient.resultLink,
        data.courseName,
        data.courseId,
        data.feedbackSessionName,
        recipient.deadline ?? data.deadline,
        data.coOwnerEmails ?? '',
        data.supportEmail ?? '',
      ]),
    ];

    return CsvHelper.convertCsvContentsToCsvString(csvRows);
  }
}
