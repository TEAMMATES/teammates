import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RequestLogDetailsComponent } from './request-log-details.component';
import { deepCopy } from '../../../../test-helpers/deep-copy';
import { generalLogEntryBuilder } from '../../../../test-helpers/log-test-helpers';
import testEventEmission from '../../../../test-helpers/test-event-emitter';
import {
  GeneralLogEntry,
  LogEvent,
  RequestLogDetails,
  RequestLogUser,
} from '../../../../types/api-output';

type TestData = {
  inputLogValue: GeneralLogEntry,
  inputLogDetails: Required<RequestLogDetails>,
  expectedLogValue: GeneralLogEntry,
  expectedLogDetails: Required<RequestLogDetails>,
  expectedRequestBodyObject: Record<string, string>,
};

describe('RequestLogDetailsComponent', () => {
  let component: RequestLogDetailsComponent;
  let fixture: ComponentFixture<RequestLogDetailsComponent>;

  const generateTestData: () => TestData = () => {
    const expectedRequestBodyString = JSON.stringify({
      testParamOne: 'testParamOneValue',
      testParamTwo: 'testParamTwoValue',
    });
    const expectedRequestBodyObject = JSON.parse(expectedRequestBodyString);
    const expectedUserInfo: RequestLogUser = {
      regkey: 'test_regkey',
      email: 'test_email@gmail.com',
      googleId: 'test_googleId',
    };
    const inputLogDetails: Required<RequestLogDetails> = {
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
      requestBody: expectedRequestBodyString,
      actionClass: 'MockAction',
      userInfo: expectedUserInfo,
      event: LogEvent.REQUEST_LOG,
      message: 'Test request log details message',
    };
    const inputLogValue: GeneralLogEntry = generalLogEntryBuilder()
      .details(inputLogDetails)
      .message('Test request log message')
      .sourceLocation({
        file: 'teammates.mock.servlets.MockApiServlet',
        line: 100,
        function: 'invokeMockServlet',
      })
      .build();
    const expectedLogDetails = deepCopy(inputLogDetails);
    const expectedLogValue = deepCopy(inputLogValue);

    return ({
      inputLogValue,
      inputLogDetails,
      expectedLogValue,
      expectedLogDetails,
      expectedRequestBodyObject,
    });

  };

  let inputLogValue: GeneralLogEntry;
  let inputLogDetails: Required<RequestLogDetails>;
  let expectedLogValue: GeneralLogEntry;
  let expectedLogDetails: Required<RequestLogDetails>;
  let expectedRequestBodyObject: Record<string, string>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RequestLogDetailsComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestLogDetailsComponent);
    component = fixture.componentInstance;

    ({
      inputLogDetails,
      inputLogValue,
      expectedLogValue,
      expectedLogDetails,
      expectedRequestBodyObject,
    } = generateTestData());
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set log value from the input log', () => {
    fixture.componentRef.setInput('log', inputLogValue);
    fixture.detectChanges();

    expect(component.logValue).toEqual(expectedLogValue);
  });

  it('addUserInfoToFilter: should emit the correct userInfo to the addUserInfoEvent', () => {
    let emittedUserInfo: RequestLogUser | undefined;
    testEventEmission(
      component.addUserInfoEvent, (val) => { emittedUserInfo = val; },
    );

    component.addUserInfoToFilter(inputLogDetails.userInfo);
    expect(emittedUserInfo).toEqual(expectedLogDetails.userInfo);
  });

  describe('input log is a request log with JSON-formatted body', () => {
    beforeEach(() => {
      fixture.componentRef.setInput('log', inputLogValue);
      fixture.detectChanges();
    });

    it('should store basic request details', () => {
      expect(component.details.event).toBe(expectedLogDetails.event);
      expect(component.details.requestUrl).toBe(expectedLogDetails.requestUrl);
      expect(component.details.responseStatus).toBe(expectedLogDetails.responseStatus);
    });

    it('should extract user info', () => {
      expect(component.details.userInfo).toBeUndefined();
      expect(component.userInfo).toEqual(expectedLogDetails.userInfo);
    });

    it('should extract request body', () => {
      expect(component.details.requestBody).toBeUndefined();
      expect(component.requestBody).toEqual(expectedRequestBodyObject);
    });
  });

  describe('input log is a request log with non-JSON request body', () => {
    let expectedNonJsonLogDetails: RequestLogDetails;

    beforeEach(() => {
      inputLogDetails = {
        ...inputLogDetails,
        requestBody: 'This is a request body that is not in a JSON format.',
      };
      expectedNonJsonLogDetails = deepCopy(inputLogDetails);
      inputLogValue = {
        ...inputLogValue,
        details: inputLogDetails,
      };
      fixture.componentRef.setInput('log', inputLogValue);
      fixture.detectChanges();
    });

    it('should extract user info but ignore request body', () => {
      expect(component.details.userInfo).toBeUndefined();
      expect(component.userInfo).toEqual(expectedNonJsonLogDetails.userInfo);

      expect(component.details.requestBody).toBe(expectedNonJsonLogDetails.requestBody);
      expect(component.requestBody).toBeUndefined();
    });
  });

  describe('input log is not a request log', () => {
    beforeEach(() => {
      inputLogValue = {
        ...inputLogValue,
        details: {
          event: LogEvent.DEFAULT_LOG,
          message: 'Test default log details message',
        },
      };
      fixture.componentRef.setInput('log', inputLogValue);
      fixture.detectChanges();
    });

    it('should not extract request details', () => {
      expect(component.userInfo).toBeUndefined();
      expect(component.requestBody).toBeUndefined();
      expect(component.details).toBeUndefined();
    });
  });
});
