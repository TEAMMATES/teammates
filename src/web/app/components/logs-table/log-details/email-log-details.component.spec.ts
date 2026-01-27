import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { EmailLogDetailsComponent } from './email-log-details.component';
import { deepCopy } from '../../../../test-helpers/deep-copy';
import { generalLogEntryBuilder } from '../../../../test-helpers/log-test-helpers';
import {
  EmailSentLogDetails,
  EmailType, GeneralLogEntry,
  LogEvent,
} from '../../../../types/api-output';

type TestData = {
  inputLogValue: GeneralLogEntry,
  expectedLogValue: GeneralLogEntry,
  expectedLogDetails: Required<EmailSentLogDetails>,
};

describe('EmailLogDetailsComponent', () => {
  let component: EmailLogDetailsComponent;
  let fixture: ComponentFixture<EmailLogDetailsComponent>;

  const generateTestData: () => TestData = () => {
    const inputLogDetails: Required<EmailSentLogDetails> = {
      event: LogEvent.EMAIL_SENT,
      message: 'Test email log details message',
      emailRecipient: 'Foo',
      emailSubject: 'This is a test subject',
      emailContent: 'This is a test email content.',
      emailType: EmailType.LOGIN,
      emailStatus: 1,
      emailStatusMessage: 'This is a test email status message',
    };
    const inputLogValue = generalLogEntryBuilder()
      .details(inputLogDetails)
      .message('Test email log message')
      .sourceLocation({
        file: 'com.mock.Mock',
        line: 100,
        function: 'handleEmail',
      })
      .build();
    const expectedLogDetails = deepCopy(inputLogDetails);
    const expectedLogValue = deepCopy(inputLogValue);

    return ({ inputLogValue, expectedLogValue, expectedLogDetails });
  };

  let inputLogValue: GeneralLogEntry;
  let expectedLogValue: GeneralLogEntry;
  let expectedLogDetails: Required<EmailSentLogDetails>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [EmailLogDetailsComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmailLogDetailsComponent);
    component = fixture.componentInstance;

    ({ inputLogValue, expectedLogValue, expectedLogDetails } = generateTestData());
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set log value from the input log', () => {
    fixture.componentRef.setInput('log', inputLogValue);
    fixture.detectChanges();

    expect(component.logValue).toEqual(expectedLogValue);
  });

  describe('input log is an email sent log', () => {
    beforeEach(() => {
      fixture.componentRef.setInput('log', inputLogValue);
      fixture.detectChanges();
    });

    it('should extract email content from details', () => {
      expect(component.emailContent).toBe(expectedLogDetails.emailContent);
      expect(component.details.emailContent).toBeUndefined();
    });

    it('should store email details other than email content', () => {
      expect(component.details.event).toBe(expectedLogDetails.event);
      expect(component.details.emailSubject).toBe(expectedLogDetails.emailSubject);
      expect(component.details.emailType).toBe(expectedLogDetails.emailType);
    });
  });

  describe('input log is not an email sent log', () => {
    beforeEach(() => {
      inputLogValue.details = {
          event: LogEvent.DEFAULT_LOG,
          message: 'Test default log detail message',
        };

      fixture.componentRef.setInput('log', inputLogValue);
      fixture.detectChanges();
    });

    it('should not extract email details', () => {
      expect(component.emailContent).toBeUndefined();
      expect(component.details).toBeUndefined();
    });
  });
});
