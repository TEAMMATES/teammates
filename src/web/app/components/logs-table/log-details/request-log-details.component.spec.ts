import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RequestLogDetailsComponent } from './request-log-details.component';
import { generalLogEntryBuilder } from '../../../../test-helpers/log-test-helpers';
import testEventEmission from '../../../../test-helpers/test-event-emitter';
import {
  GeneralLogEntry,
  LogEvent,
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
  const baseExpectedRequestBodyObject = JSON.parse(
    baseExpectedRequestBodyString,
  );
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
  const baseExpectedLogValue: GeneralLogEntry = generalLogEntryBuilder()
    .details(baseInitialLogDetails)
    .message('Test request log message')
    .sourceLocation({
      file: 'teammates.mock.servlets.MockApiServlet',
      line: 100,
      function: 'invokeMockServlet',
    })
    .build();

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RequestLogDetailsComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(RequestLogDetailsComponent);
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

  it('addUserInfoToFilter: should emit the correct userInfo to the addUserInfoEvent', () => {
    let emittedUserInfo: RequestLogUser | undefined;
    const expectedUserInfo: RequestLogUser = {
      regkey: 'test-regkey',
      email: 'test@example.com',
      googleId: 'test-googleId',
    };
    testEventEmission(
      component.addUserInfoEvent, (val) => { emittedUserInfo = val; },
    );

    component.addUserInfoToFilter(expectedUserInfo);
    expect(emittedUserInfo).toBe(expectedUserInfo);
  });

  describe('input log is a request log with JSON-formatted body', () => {
    beforeEach(() => {
      fixture.componentRef.setInput('log', baseExpectedLogValue);
      fixture.detectChanges();
    });

    it('should store basic request details', () => {
      expect(component.details.event).toBe(LogEvent.REQUEST_LOG);
      expect(component.details.requestUrl).toBe(baseInitialLogDetails.requestUrl);
      expect(component.details.responseStatus).toBe(baseInitialLogDetails.responseStatus);
    });

    it('should extract user info', () => {
      expect(component.details.userInfo).toBeUndefined();
      expect(component.userInfo).toEqual(baseExpectedUserInfo);
    });

    it('should extract request body', () => {
      expect(component.details.requestBody).toBeUndefined();
      expect(component.requestBody).toEqual(baseExpectedRequestBodyObject);
    });
  });

  describe('input log is a request log with non-JSON request body', () => {
    const initialLogDetails = {
      ...baseInitialLogDetails,
      requestBody: 'This is a request body that is not in a JSON format.',
    };
    const expectedLogValue: GeneralLogEntry = {
      ...baseExpectedLogValue,
      details: initialLogDetails,
    };

    beforeEach(() => {
      fixture.componentRef.setInput('log', expectedLogValue);
      fixture.detectChanges();
    });

    it('should extract user info but ignore request body', () => {
      expect(component.details.userInfo).toBeUndefined();
      expect(component.userInfo).toEqual(initialLogDetails.userInfo);

      expect(component.details.requestBody).toBe(initialLogDetails.requestBody);
      expect(component.requestBody).toBeUndefined();
    });
  });

  describe('input log is not a request log', () => {
    const nonRequestLog: GeneralLogEntry = {
      ...baseExpectedLogValue,
      details: {
        event: LogEvent.DEFAULT_LOG,
        message: 'Test default log detail message',
      },
    };

    beforeEach(() => {
      fixture.componentRef.setInput('log', nonRequestLog);
      fixture.detectChanges();
    });

    it('should not extract request details', () => {
      expect(component.userInfo).toBeUndefined();
      expect(component.requestBody).toBeUndefined();
      expect(component.details).toBeUndefined();
    });
  });
});
