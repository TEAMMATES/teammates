import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { GeneralLogEntry, LogEvent, LogSeverity } from '../../../../types/api-output';
import { EmailLogDetailsComponent } from './email-log-details.component';

describe('EmailLogDetailsComponent', () => {
  let component: EmailLogDetailsComponent;
  let fixture: ComponentFixture<EmailLogDetailsComponent>;
  let mockLog: GeneralLogEntry;

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

    // Set mock log values with more realistic data
    mockLog = {
      severity: LogSeverity.INFO,
      trace: 'trace_id_123',
      insertId: 'unique_insert_id_456', // a unique insert ID
      resourceIdentifier: { id: 'resource123' }, // resource identifier
      sourceLocation: { file: 'test_log.txt', line: 123, function: '' }, // source location
      timestamp: Date.now(), // Current timestamp
      message: 'Email sent successfully', // log message
      details: {
        event: LogEvent.EMAIL_SENT,
        message: 'Email was sent successfully',
      },
    };
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  describe('Non-EMAIL_SENT Events Set Test', () => {
    const logEventRequest = LogEvent.REQUEST_LOG;
    const modifiedLogRequest = { ...mockLog, details: { event: logEventRequest, message: '' } };
    it(`should not set details for non EMAIL_SENT events - ${logEventRequest}`, () => {
      component.log = modifiedLogRequest;
      expect(component.logValue).toEqual(modifiedLogRequest);
      expect(component.details).toBeUndefined();
    });

    const logEventEXCEPTION = LogEvent.EXCEPTION_LOG;
    const modifiedLogEXCEPTION = { ...mockLog, details: { event: logEventEXCEPTION, message: '' } };
    it(`should not set details for non EMAIL_SENT events - ${logEventEXCEPTION}`, () => {
      component.log = modifiedLogEXCEPTION;
      expect(component.logValue).toEqual(modifiedLogEXCEPTION);
      expect(component.details).toBeUndefined();
    });

    const logEventINSTANCE = LogEvent.INSTANCE_LOG;
    const modifiedLogINSTANCE = { ...mockLog, details: { event: logEventINSTANCE, message: '' } };
    it(`should not set details for non EMAIL_SENT events - ${logEventINSTANCE}`, () => {
      component.log = modifiedLogINSTANCE;
      expect(component.logValue).toEqual(modifiedLogINSTANCE);
      expect(component.details).toBeUndefined();
    });

    const logEventFEEDBACK = LogEvent.FEEDBACK_SESSION_AUDIT;
    const modifiedLogFEEDBACK = { ...mockLog, details: { event: logEventFEEDBACK, message: '' } };
    it(`should not set details for non EMAIL_SENT events - ${logEventFEEDBACK}`, () => {
      component.log = modifiedLogFEEDBACK;
      expect(component.logValue).toEqual(modifiedLogFEEDBACK);
      expect(component.details).toBeUndefined();
    });

    const logEventDEFAULT = LogEvent.DEFAULT_LOG;
    const modifiedLogDEFAULT = { ...mockLog, details: { event: logEventDEFAULT, message: '' } };
    it(`should not set details for non EMAIL_SENT events - ${logEventDEFAULT}`, () => {
      component.log = modifiedLogDEFAULT;
      expect(component.logValue).toEqual(modifiedLogDEFAULT);
      expect(component.details).toBeUndefined();
    });

  });
  describe('Log Entry Manipulation', () => {
    it('should set log value and details for EMAIL_SENT event', () => {
      component.log = { ...mockLog };
      expect(component.logValue).toEqual(mockLog);
      expect(component.details).toEqual(mockLog.details);
    });

    it('should assign and retrieve a valid GeneralLogEntry', () => {
      const modifiedLog = { ...mockLog, severity: LogSeverity.INFO, details: undefined };
      component.logValue = modifiedLog;
      expect(component.log).toEqual(modifiedLog);
    });
  });
});
