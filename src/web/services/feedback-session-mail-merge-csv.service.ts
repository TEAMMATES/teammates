import { Injectable } from '@angular/core';
import { CsvHelper } from './csv-helper';

export interface FeedbackSessionMailMergeData {
  courseId: string;
  courseName: string;
  feedbackSessionName: string;
  deadline: string;
  coOwnerEmails?: string;
  supportEmail?: string;
}

export interface FeedbackSessionSubmissionMailMergeData extends FeedbackSessionMailMergeData {
  sessionInstructions: string;
  recipients: FeedbackSessionSubmissionMailMergeRecipient[];
}

export interface FeedbackSessionResultMailMergeData extends FeedbackSessionMailMergeData {
  recipients: FeedbackSessionResultMailMergeRecipient[];
}

export interface FeedbackSessionSubmissionMailMergeRecipient {
  email: string;
  name: string;
  submissionLink: string;
  deadline?: string;
}

export interface FeedbackSessionResultMailMergeRecipient {
  email: string;
  name: string;
  resultLink: string;
  deadline?: string;
}

/**
 * Service to generate mail merge CSV data for feedback session emails.
 */
@Injectable({
  providedIn: 'root',
})
export class FeedbackSessionMailMergeCsvService {
  getCsvForSubmissionLinks(data: FeedbackSessionSubmissionMailMergeData): string {
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
      ...data.recipients.map((recipient: FeedbackSessionSubmissionMailMergeRecipient) => [
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

  getCsvForResultLinks(data: FeedbackSessionResultMailMergeData): string {
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
      ...data.recipients.map((recipient: FeedbackSessionResultMailMergeRecipient) => [
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
