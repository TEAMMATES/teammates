import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RequestLogDetailsComponent } from './request-log-details.component';
import {
  GeneralLogEntry,
  LogEvent,
  LogSeverity,
  RequestLogDetails,
  RequestLogUser,
} from '../../../../types/api-output';

describe('RequestLogDetailsComponent', () => {
  let component: RequestLogDetailsComponent;
  let fixture: ComponentFixture<RequestLogDetailsComponent>;

  const baseExpectedRequestBodyString = JSON.stringify({
    testParamOne: 'testParamOneValue',
    testParamTwo: 'testParamTwoValue',
  });
  const baseExpectedRequestBodyObject = JSON.parse(baseExpectedRequestBodyString);
  const baseExpectedUserInfo: RequestLogUser = {
    regkey: 'test_regkey',
    email: 'test_email@gmail.com',
    googleId: 'test_googleId',
  };
  const baseInitialLogDetails: RequestLogDetails = {
    responseStatus: 200,
    responseTime: 20,
    requestMethod: 'GET',
    requestUrl: '/mock_api/mock_status',
    userAgent: 'Mockzilla/1.1',
    instanceId: '0123456789abcdef',
    webVersion: '1.1.1',
    referrer: '',
    requestParams: {
      testParamOne: 'testParamOneValue',
      testParamTwo: 'testParamTwoValue',
    },
    requestHeaders: {
      testHeaderOne: 'testHeaderOneValue',
      testHeaderTwo: 'testHeaderTwoValue',
    },
    requestBody: baseExpectedRequestBodyString,
    actionClass: 'MockAction',
    userInfo: baseExpectedUserInfo,
    event: LogEvent.REQUEST_LOG,
    message: 'Test request log details message',
  };
  const baseExpectedLogDetails: RequestLogDetails = {
    ...baseInitialLogDetails,
    userInfo: undefined,
    requestBody: undefined,
  };
  const baseExpectedLogValue: GeneralLogEntry = {
    severity: LogSeverity.DEFAULT,
    trace: '0123456789abcdef',
    insertId: '0123456789abcdef',
    resourceIdentifier: {
      module_id: 'mock',
      version_id: '1-0-0',
      project_id: 'mock-project',
      zone: 'mock-zone-1',
    },
    sourceLocation: {
      file: 'teammates.mock.servlets.MockApiServlet',
      line: 100,
      function: 'invokeMockServlet',
    },
    timestamp: 1000,
    message: 'Test request log message',
    details: baseInitialLogDetails,
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RequestLogDetailsComponent],
    }).compileComponents();
  }));

  describe('input log entry is a request log with JSON-formatted body', () => {
    beforeEach(waitForAsync(() => {
      fixture = TestBed.createComponent(RequestLogDetailsComponent);
      component = fixture.componentInstance;
      fixture.componentRef.setInput('log', baseExpectedLogValue);
      fixture.detectChanges();
      fixture.whenStable();
    }));

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should set the input log as log value', () => {
      expect(component.logValue).toEqual(baseExpectedLogValue);
    });

    it('should parse request body of JSON format', () => {
      expect(component.requestBody).toEqual(baseExpectedRequestBodyObject);
    });

    it('should move user info and request body from the input log to the component attributes', () => {
      expect(component.details).toEqual(baseExpectedLogDetails);
      expect(component.userInfo).toEqual(baseExpectedUserInfo);
      expect(component.requestBody).toEqual(baseExpectedRequestBodyObject);
    });
  });

  describe('input log entry is not a request log', () => {
    let component: RequestLogDetailsComponent;
    let fixture: ComponentFixture<RequestLogDetailsComponent>;
    const nonRequestLog: GeneralLogEntry = {
      ...baseExpectedLogValue,
      details: {
        ...baseInitialLogDetails,
        event: LogEvent.DEFAULT_LOG,
      }
    }

    beforeEach(waitForAsync(() => {
      fixture = TestBed.createComponent(RequestLogDetailsComponent);
      component = fixture.componentInstance;
      fixture.componentRef.setInput('log', nonRequestLog);
      fixture.detectChanges();
      fixture.whenStable();
    }));

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should not update any component attributes except logValue', () => {
      expect(component.logValue).toEqual(nonRequestLog);
      expect(component.details).toBeUndefined();
      expect(component.userInfo).toBeUndefined();
      expect(component.requestBody).toBeUndefined();
    });
  });

  describe('input log entry is a request log with non-JSON request body', () => {
    let component: RequestLogDetailsComponent;
    let fixture: ComponentFixture<RequestLogDetailsComponent>;

    const initialLogDetails = {
      ...baseInitialLogDetails,
      requestBody: 'This is a request body that is not in a JSON format.',
    }
    const expectedLogDetails: RequestLogDetails = {
      ...initialLogDetails,
      userInfo: undefined,
    }
    const expectedLogValue: GeneralLogEntry = {
      ...baseExpectedLogValue,
      details: initialLogDetails,
    }

    beforeEach(waitForAsync(() => {
      fixture = TestBed.createComponent(RequestLogDetailsComponent);
      component = fixture.componentInstance;
      fixture.componentRef.setInput('log', expectedLogValue);
      fixture.detectChanges();
      fixture.whenStable();
    }));

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should move user info from the input log to the component attributes', () => {
      expect(component.details).toEqual(expectedLogDetails);
      expect(component.userInfo).toEqual(baseExpectedUserInfo);
      expect(component.requestBody).toBeUndefined();
    });
  });
});
