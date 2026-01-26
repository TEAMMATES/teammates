import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { EmailLogDetailsComponent } from './email-log-details.component';
import { generalLogEntryBuilder } from '../../../../test-helpers/log-test-helpers';
import {
  EmailSentLogDetails,
  EmailType, GeneralLogEntry,
  LogEvent,
} from '../../../../types/api-output';

describe('EmailLogDetailsComponent', () => {
  let component: EmailLogDetailsComponent;
  let fixture: ComponentFixture<EmailLogDetailsComponent>;

  const baseExpectedEmailContent = 'This is a test email content.';
  const baseInitialLogDetails = {
    event: LogEvent.EMAIL_SENT,
    message: 'Test email log details message',
    emailRecipient: 'Foo',
    emailSubject: 'This is a test subject',
    emailContent: baseExpectedEmailContent,
    emailType: EmailType.LOGIN,
    emailStatus: 1,
    emailStatusMessage: 'This is a test email status message',
  } as EmailSentLogDetails;
  const baseExpectedLogValue = generalLogEntryBuilder()
    .details(baseInitialLogDetails)
    .message('Test email log message')
    .sourceLocation({
      file: 'com.mock.Mock',
      line: 100,
      function: 'handleEmail',
    })
    .build();

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [EmailLogDetailsComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(EmailLogDetailsComponent);
    component = fixture.componentInstance;
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set log value from the input log', () => {
    fixture.componentRef.setInput('log', baseExpectedLogValue);
    fixture.detectChanges();

    expect(component.logValue).toBe(baseExpectedLogValue);
  });

  describe('input log is an email sent log', () => {
    beforeEach(() => {
      fixture.componentRef.setInput('log', baseExpectedLogValue);
      fixture.detectChanges();
    });

    it('should extract email details', () => {
      expect(component.details.emailContent).toBeUndefined();
      expect(component.emailContent).toBe(baseExpectedEmailContent);
    });
  });

  describe('input log is not an email sent log', () => {
    const expectedLogValue: GeneralLogEntry = {
      ...baseExpectedLogValue,
      details: {
        event: LogEvent.DEFAULT_LOG,
        message: 'Test default log detail message',
      },
    };

    beforeEach(() => {
      fixture.componentRef.setInput('log', expectedLogValue);
      fixture.detectChanges();
    });

    it('should not extract email details', () => {
      expect(component.emailContent).toBeUndefined();
      expect(component.details).toBeUndefined();
    });
  });
});
