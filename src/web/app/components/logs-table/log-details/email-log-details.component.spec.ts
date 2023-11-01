import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { GeneralLogEntry, LogEvent, LogSeverity } from '../../../../types/api-output';
import { EmailLogDetailsComponent } from './email-log-details.component';

describe('EmailLogDetailsComponent', () => {
  let component: EmailLogDetailsComponent;
  let fixture: ComponentFixture<EmailLogDetailsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [EmailLogDetailsComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmailLogDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set log value and details when log is set', () => {
    const log: GeneralLogEntry = {
      severity: LogSeverity.DEFAULT,
      trace: 'test_trace',
      insertId: '1',
      resourceIdentifier: { id: '2' },
      sourceLocation: { file: 'log.txt', line: 42, function: 'test_function' },
      timestamp: Date.now(),
      message: 'test_message',
      details: {
        event: LogEvent.EMAIL_SENT,
        message: 'email_sent',
      },
    };

    component.log = log;

    expect(component.logValue).toEqual(log);
    expect(component.details).toEqual(log.details);
  });

  it('should not set details when log details event is not EMAIL_SENT', () => {
    const log: GeneralLogEntry = {
      severity: LogSeverity.DEFAULT,
      trace: 'test_trace',
      insertId: '1',
      resourceIdentifier: { id: '2' },
      sourceLocation: { file: 'log.txt', line: 42, function: 'test_function' },
      timestamp: Date.now(),
      message: 'test_message',
      details: {
        event: LogEvent.EXCEPTION_LOG,
        message: 'email_sent',
      },
    };

    component.log = log;

    expect(component.logValue).toEqual(log);
    expect(component.details).toBeUndefined();
  });

  it('should return a valid GeneralLogEntry', () => {
    const log: GeneralLogEntry = {
      severity: LogSeverity.INFO,
      trace: 'test_trace',
      insertId: '1',
      resourceIdentifier: {},
      sourceLocation: { file: 'log.txt', line: 42, function: 'test_function' },
      timestamp: Date.now(),
      details: undefined,
    };

    component.logValue = log;
    expect(component.log).toEqual(log);
  });

  it('should handle emailContent correctly', () => {
  const log: GeneralLogEntry = {
    severity: LogSeverity.DEFAULT,
    trace: 'test_trace',
    insertId: '1',
    resourceIdentifier: { id: '2' },
    sourceLocation: { file: 'log.txt', line: 42, function: 'test_function' },
    timestamp: Date.now(),
    message: 'test_message',
    details: {
      event: LogEvent.EMAIL_SENT,
      message: 'email_sent',
      emailContent: 'This is the email content',
    },
  };

  component.log = log;

  expect(component.logValue).toEqual(log);
  expect(component.details.emailContent).toBeUndefined();
  expect(component.emailContent).toBe(log.details.emailContent);
});


});
