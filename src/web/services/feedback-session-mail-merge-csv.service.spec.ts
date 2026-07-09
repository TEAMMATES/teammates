import { TestBed } from '@angular/core/testing';
import { FeedbackSessionMailMergeCsvService } from './feedback-session-mail-merge-csv.service';

describe('FeedbackSessionMailMergeCsvService', () => {
  let service: FeedbackSessionMailMergeCsvService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FeedbackSessionMailMergeCsvService);
  });

  it('should generate submission link mail merge csv', () => {
    const result: string = service.getCsvForSubmissionLinks({
      courseId: 'CS101',
      courseName: 'Programming',
      feedbackSessionName: 'Week 1 Feedback',
      deadline: 'Fri, 10 Jul 2026, 11:59 PM',
      sessionInstructions: 'Please answer all questions.',
      coOwnerEmails: 'instructor@example.com',
      supportEmail: 'teammates@example.com',
      recipients: [
        {
          email: 'student@example.com',
          name: 'Alex Tan',
          submissionLink: 'https://example.com/submit',
        },
      ],
    });

    expect(result).toEqual(
      [
        'Email,Name,Submission Link,Course Name,Course ID,Feedback Session Name,Deadline,Session Instructions,Co-owner Emails,Support Email',
        'student@example.com,Alex Tan,https://example.com/submit,Programming,CS101,Week 1 Feedback,"Fri, 10 Jul 2026, 11:59 PM",Please answer all questions.,instructor@example.com,teammates@example.com',
      ].join('\r\n'),
    );
  });

  it('should generate result link mail merge csv', () => {
    const result: string = service.getCsvForResultLinks({
      courseId: 'CS101',
      courseName: 'Programming',
      feedbackSessionName: 'Week 1 Feedback',
      deadline: 'Fri, 10 Jul 2026, 11:59 PM',
      coOwnerEmails: 'instructor@example.com',
      supportEmail: 'teammates@example.com',
      recipients: [
        {
          email: 'student@example.com',
          name: 'Alex Tan',
          resultLink: 'https://example.com/result',
        },
      ],
    });

    expect(result).toEqual(
      [
        'Email,Name,Result Link,Course Name,Course ID,Feedback Session Name,Deadline,Co-owner Emails,Support Email',
        'student@example.com,Alex Tan,https://example.com/result,Programming,CS101,Week 1 Feedback,"Fri, 10 Jul 2026, 11:59 PM",instructor@example.com,teammates@example.com',
      ].join('\r\n'),
    );
  });

  it('should generate one data row for each recipient', () => {
    const result: string = service.getCsvForResultLinks({
      courseId: 'CS101',
      courseName: 'Programming',
      feedbackSessionName: 'Week 1 Feedback',
      deadline: 'Fri, 10 Jul 2026, 11:59 PM',
      recipients: [
        {
          email: 'student1@example.com',
          name: 'Alex Tan',
          resultLink: 'https://example.com/result-1',
        },
        {
          email: 'student2@example.com',
          name: 'Bala Lee',
          resultLink: 'https://example.com/result-2',
        },
      ],
    });

    expect(result).toEqual(
      [
        'Email,Name,Result Link,Course Name,Course ID,Feedback Session Name,Deadline,Co-owner Emails,Support Email',
        'student1@example.com,Alex Tan,https://example.com/result-1,Programming,CS101,Week 1 Feedback,"Fri, 10 Jul 2026, 11:59 PM",,',
        'student2@example.com,Bala Lee,https://example.com/result-2,Programming,CS101,Week 1 Feedback,"Fri, 10 Jul 2026, 11:59 PM",,',
      ].join('\r\n'),
    );
  });

  it('should use recipient-specific deadlines when present', () => {
    const result: string = service.getCsvForSubmissionLinks({
      courseId: 'CS101',
      courseName: 'Programming',
      feedbackSessionName: 'Week 1 Feedback',
      deadline: 'Default deadline',
      sessionInstructions: 'Please answer all questions.',
      recipients: [
        {
          email: 'student@example.com',
          name: 'Alex Tan',
          submissionLink: 'https://example.com/submit',
          deadline: 'Extended deadline',
        },
      ],
    });

    expect(result).toContain('Extended deadline');
    expect(result).not.toContain('Default deadline');
  });

  it('should leave optional contact fields empty when omitted', () => {
    const result: string = service.getCsvForResultLinks({
      courseId: 'CS101',
      courseName: 'Programming',
      feedbackSessionName: 'Week 1 Feedback',
      deadline: 'Fri, 10 Jul 2026, 11:59 PM',
      recipients: [
        {
          email: 'student@example.com',
          name: 'Alex Tan',
          resultLink: 'https://example.com/result',
        },
      ],
    });

    expect(result).toEqual(
      [
        'Email,Name,Result Link,Course Name,Course ID,Feedback Session Name,Deadline,Co-owner Emails,Support Email',
        'student@example.com,Alex Tan,https://example.com/result,Programming,CS101,Week 1 Feedback,"Fri, 10 Jul 2026, 11:59 PM",,',
      ].join('\r\n'),
    );
  });

  it('should escape csv fields', () => {
    const result: string = service.getCsvForSubmissionLinks({
      courseId: 'CS101',
      courseName: 'Programming, "Basics"',
      feedbackSessionName: 'Week 1\r\nFeedback',
      deadline: 'Fri, 10 Jul 2026, 11:59 PM',
      sessionInstructions: 'Line 1\nLine 2',
      recipients: [
        {
          email: 'student@example.com',
          name: 'Alex "A" Tan',
          submissionLink: 'https://example.com/submit?a=1,b=2',
        },
      ],
    });

    expect(result).toEqual(
      [
        'Email,Name,Submission Link,Course Name,Course ID,Feedback Session Name,Deadline,Session Instructions,Co-owner Emails,Support Email',
        'student@example.com,"Alex ""A"" Tan","https://example.com/submit?a=1,b=2","Programming, ""Basics""",CS101,"Week 1\r\nFeedback","Fri, 10 Jul 2026, 11:59 PM","Line 1\nLine 2",,',
      ].join('\r\n'),
    );
  });
});
