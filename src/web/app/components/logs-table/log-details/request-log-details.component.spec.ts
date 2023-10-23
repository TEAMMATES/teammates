import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import {
  GeneralLogEntry,
  LogEvent,
  LogSeverity,
  RequestLogDetails,
} from '../../../../types/api-output';
import { RequestLogDetailsComponent } from './request-log-details.component';

describe('RequestLogDetailsComponent', () => {
  let component: RequestLogDetailsComponent;
  let fixture: ComponentFixture<RequestLogDetailsComponent>;
  let requestLog: GeneralLogEntry;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RequestLogDetailsComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestLogDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    // Common initialization for a REQUEST_LOG event.
    requestLog = {
      severity: LogSeverity.INFO,
      trace: 'trace_id_123',
      insertId: 'unique_insert_id_456',
      resourceIdentifier: { resource: 'sample' },
      sourceLocation: { file: 'test_log.txt', line: 123, function: '' },
      timestamp: Date.now(),
      details: {
        event: LogEvent.REQUEST_LOG,
        // responseStatus: 200,
        // responseTime: 100,
        // requestMethod: '',
        // requestUrl: '',
        // userAgent: '',
        // instanceId: '',
        // webVersion: '',
        // referrer: '',
        // requestParams: { param1: '', param2: '' },
        // requestHeaders: { header1: '', header2: '' },
        // requestBody: '{}',
        // actionClass: '',
        // userInfo: { userId: '', username: '' },
      },
    };
  });

  describe('Initialization', () => {
    it('should instantiate the component', () => {
      expect(component).toBeTruthy();
    });
  });

  describe('Setting and Retrieving Logs', () => {
    it('should correctly set and retrieve the log and details', () => {
      const log = requestLog;
      const requestLogDetails: RequestLogDetails = {
        event: LogEvent.REQUEST_LOG,
        responseStatus: 200,
        responseTime: 123,
        requestMethod: '',
        requestUrl: '',
        userAgent: '',
        instanceId: '',
        webVersion: '',
        referrer: '',
        userInfo: { regkey: '', email: '', googleId: '' },
      };
      log.details = requestLogDetails;

      component.log = log;
      const details: RequestLogDetails = JSON.parse(
        JSON.stringify(log.details),
      ) as RequestLogDetails;
      component.userInfo = details.userInfo;
      details.userInfo = undefined;
      component.details = details;

      expect(component.logValue).toEqual(log);
      expect(component.details).toEqual(details);
      expect(component.userInfo).toEqual(requestLogDetails.userInfo);
      expect(component.requestBody).toEqual(undefined);
    });

    it('should assign and retrieve a valid GeneralLogEntry', () => {
      const log: GeneralLogEntry = {
        severity: LogSeverity.INFO,
        trace: 'trace_id_123',
        insertId: 'unique_insert_id_456',
        resourceIdentifier: {},
        sourceLocation: { file: 'test_log.txt', line: 123, function: '' },
        timestamp: Date.now(),
        details: undefined,
      };
      component.logValue = log;
      expect(component.log).toEqual(log);
    });
  });

  describe('Non-REQUEST_LOG Events', () => {
    const logEventEXCEPTION = LogEvent.EXCEPTION_LOG;
    const modifiedLogEXCEPTION = {
      ...requestLog,
      details: {
        event: logEventEXCEPTION,
      },
    };
    it(`should not set details for non REQUEST_LOG events - ${logEventEXCEPTION}`, () => {
      component.log = modifiedLogEXCEPTION;
      expect(component.logValue).toEqual(modifiedLogEXCEPTION);
      expect(component.details).toBeUndefined();
      expect(component.userInfo).toBeUndefined();
      expect(component.requestBody).toBeUndefined();
    });

    const logEventINSTANCE = LogEvent.INSTANCE_LOG;
    const modifiedLogINSTANCE = {
      ...requestLog,
      details: {
        event: logEventEXCEPTION,
      },
    };
    it(`should not set details for non REQUEST_LOG events - ${logEventINSTANCE}`, () => {
      component.log = modifiedLogINSTANCE;
      expect(component.logValue).toEqual(modifiedLogINSTANCE);
      expect(component.details).toBeUndefined();
      expect(component.userInfo).toBeUndefined();
      expect(component.requestBody).toBeUndefined();
    });

    const logEventEMAIL = LogEvent.EMAIL_SENT;
    const modifiedLogEMAIL = {
      ...requestLog,
      details: {
        event: logEventEMAIL,
      },
    };
    it(`should not set details for non REQUEST_LOG events - ${logEventEMAIL}`, () => {
      component.log = modifiedLogEMAIL;
      expect(component.logValue).toEqual(modifiedLogEMAIL);
      expect(component.details).toBeUndefined();
      expect(component.userInfo).toBeUndefined();
      expect(component.requestBody).toBeUndefined();
    });

    const logEventFEEDBACK = LogEvent.FEEDBACK_SESSION_AUDIT;
    const modifiedLogFEEDBACK = {
      ...requestLog,
      details: {
        event: logEventFEEDBACK,
      },
    };
    it(`should not set details for non REQUEST_LOG events - ${logEventFEEDBACK}`, () => {
      component.log = modifiedLogFEEDBACK;
      expect(component.logValue).toEqual(modifiedLogFEEDBACK);
      expect(component.details).toBeUndefined();
      expect(component.userInfo).toBeUndefined();
      expect(component.requestBody).toBeUndefined();
    });

    const logEventDEFAULT = LogEvent.DEFAULT_LOG;
    const modifiedLogDEFAULT = {
      ...requestLog,
      details: {
        event: logEventDEFAULT,
      },
    };
    it(`should not set details for non REQUEST_LOG events - ${logEventDEFAULT}`, () => {
      component.log = modifiedLogDEFAULT;
      expect(component.logValue).toEqual(modifiedLogDEFAULT);
      expect(component.details).toBeUndefined();
      expect(component.userInfo).toBeUndefined();
      expect(component.requestBody).toBeUndefined();
    });
  });

  describe('Emitting Events', () => {
    it('should emit the user info on addUserInfoToFilter invocation', () => {
      const emitSpy = jest.spyOn(component.addUserInfoEvent, 'emit');
      const userInfo = { regkey: '', email: '', googleId: '' };

      component.addUserInfoToFilter(userInfo);

      expect(emitSpy).toHaveBeenCalledWith(userInfo);
      emitSpy.mockRestore();
    });
  });
});
