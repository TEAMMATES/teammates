import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ExceptionLogDetailsComponent } from './exception-log-details.component';
import { deepCopy } from '../../../../test-helpers/deep-copy';
import { generalLogEntryBuilder } from '../../../../test-helpers/log-test-helpers';
import {
  ExceptionLogDetails,
  GeneralLogEntry,
  LogEvent,
} from '../../../../types/api-output';

type TestData = {
  inputLogValue: GeneralLogEntry,
  inputLogDetails: Required<ExceptionLogDetails>,
  expectedLogValue: GeneralLogEntry,
  expectedLogDetails: Required<ExceptionLogDetails>,
  expectedExceptionStackTraceString: string,
};

describe('ExceptionLogDetailsComponent', () => {
  let component: ExceptionLogDetailsComponent;
  let fixture: ComponentFixture<ExceptionLogDetailsComponent>;

  const generateTestData: () => TestData = () => {
    const inputLogDetails: Required<ExceptionLogDetails> = {
      event: LogEvent.EXCEPTION_LOG,
      message: 'Test exception log details message',
      exceptionClass: 'MockException',
      exceptionClasses: [
        'com.mock.MockException',
        'com.mock.MockFooException',
        'com.mock.MockBarException',
      ],
      exceptionStackTraces: [
        [
          'com.mock.Mock.run(Mock.java:5)',
          'com.mock.Mock.tryRun(Mock.java:10)',
          'com.mock.Mock.setup(Mock.java:50)',
        ],
        [
          'com.mock.MockFoo.runFoo(MockFoo.java:5)',
          'com.mock.MockFoo.tryRunFoo(MockFoo.java:10)',
          'com.mock.MockFoo.setupFoo(MockFoo.java:50)',
        ],
        [
          'com.mock.MockBar.runBar(MockBar.java:5)',
          'com.mock.MockBar.tryRunBar(MockBar.java:10)',
          'com.mock.MockBar.setupBar(MockBar.java:50)',
        ],
      ],
      exceptionMessages: [
        'Mock exception message',
        'MockFoo exception message',
        'MockBar exception message',
      ],
      loggerSourceLocation: {
        file: 'com.mock.Mock',
        line: 100,
        function: 'handleException',
      },
    };
    const inputLogValue: GeneralLogEntry = generalLogEntryBuilder()
      .details(inputLogDetails)
      .message('Test exception log message')
      .sourceLocation({
        file: 'com.mock.Mock',
        line: 100,
        function: 'run',
      })
      .build();
    const expectedLogDetails = deepCopy(inputLogDetails);
    const expectedLogValue = deepCopy(inputLogValue);
    const expectedExceptionStackTraceString =
      'com.mock.MockException: Mock exception message'
      + '\r\n        at com.mock.Mock.run(Mock.java:5)'
      + '\r\n        at com.mock.Mock.tryRun(Mock.java:10)'
      + '\r\n        at com.mock.Mock.setup(Mock.java:50)'
      + '\r\ncom.mock.MockFooException: MockFoo exception message'
      + '\r\n        at com.mock.MockFoo.runFoo(MockFoo.java:5)'
      + '\r\n        at com.mock.MockFoo.tryRunFoo(MockFoo.java:10)'
      + '\r\n        at com.mock.MockFoo.setupFoo(MockFoo.java:50)'
      + '\r\ncom.mock.MockBarException: MockBar exception message'
      + '\r\n        at com.mock.MockBar.runBar(MockBar.java:5)'
      + '\r\n        at com.mock.MockBar.tryRunBar(MockBar.java:10)'
      + '\r\n        at com.mock.MockBar.setupBar(MockBar.java:50)';

    return ({
      inputLogDetails,
      inputLogValue,
      expectedLogValue,
      expectedLogDetails,
      expectedExceptionStackTraceString,
    });
  };

  let inputLogValue: GeneralLogEntry;
  let inputLogDetails: Required<ExceptionLogDetails>;
  let expectedLogValue: GeneralLogEntry;
  let expectedLogDetails: Required<ExceptionLogDetails>;
  let expectedExceptionStackTraceString: string;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ExceptionLogDetailsComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExceptionLogDetailsComponent);
    component = fixture.componentInstance;

    ({
      inputLogDetails,
      inputLogValue,
      expectedLogValue,
      expectedLogDetails,
      expectedExceptionStackTraceString,
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

  describe('input log is a valid exception log', () => {
    beforeEach(() => {
      fixture.componentRef.setInput('log', inputLogValue);
      fixture.detectChanges();
    });

    it('should store basic exception details', () => {
      expect(component.details.event).toBe(expectedLogDetails.event);
      expect(component.details.exceptionClass).toBe(expectedLogDetails.exceptionClass);
      expect(component.details.message).toBe(expectedLogDetails.message);
    });

    it('should have a properly formatted stack trace string', () => {
      expect(component.exceptionStackTrace).toBe(expectedExceptionStackTraceString);
    });

    it('should remove exception classes, messages, and stack traces from details', () => {
      expect(component.details.exceptionClasses).toBeUndefined();
      expect(component.details.exceptionMessages).toBeUndefined();
      expect(component.details.exceptionStackTraces).toBeUndefined();
    });
  });

  describe('input log is not an exception log', () => {
    beforeEach(() => {
      inputLogValue.details = {
          event: LogEvent.DEFAULT_LOG,
          message: 'Test default log details message',
        };

      fixture.componentRef.setInput('log', inputLogValue);
      fixture.detectChanges();
    });

    it('should not extract exception details', () => {
      expect(component.details).toBeUndefined();
      expect(component.exceptionStackTrace).toBeUndefined();
    });
  });

  describe('input log is an invalid exception log with wrong number of exception classes', () => {
    let expectedInvalidLogDetails: ExceptionLogDetails;

    beforeEach(() => {
      inputLogDetails.exceptionClasses = inputLogDetails.exceptionClasses.slice(1);
      expectedInvalidLogDetails = deepCopy(inputLogDetails);
      inputLogValue.details = inputLogDetails;

      fixture.componentRef.setInput('log', inputLogValue);
      fixture.detectChanges();
    });

    it('should have an empty exception stack trace string', () => {
      expect(component.exceptionStackTrace).toBe('');
    });

    it('should preserve all exception details in the log details', () => {
      expect(component.details.exceptionClasses).toEqual(expectedInvalidLogDetails.exceptionClasses);
      expect(component.details.exceptionMessages).toEqual(expectedInvalidLogDetails.exceptionMessages);
      expect(component.details.exceptionStackTraces).toEqual(expectedInvalidLogDetails.exceptionStackTraces);
    });
  });

  describe('input log is an invalid exception log with wrong number of exception messages', () => {
    let expectedInvalidLogDetails: ExceptionLogDetails;

    beforeEach(() => {
      inputLogDetails.exceptionMessages = inputLogDetails.exceptionMessages.slice(1);
      expectedInvalidLogDetails = deepCopy(inputLogDetails);
      inputLogValue.details = inputLogDetails;

      fixture.componentRef.setInput('log', inputLogValue);
      fixture.detectChanges();
    });

    it('should have an empty exception stack trace string', () => {
      expect(component.exceptionStackTrace).toBe('');
    });

    it('should preserve all exception details in the log details', () => {
      expect(component.details.exceptionClasses).toEqual(expectedInvalidLogDetails.exceptionClasses);
      expect(component.details.exceptionMessages).toEqual(expectedInvalidLogDetails.exceptionMessages);
      expect(component.details.exceptionStackTraces).toEqual(expectedInvalidLogDetails.exceptionStackTraces);
    });
  });
});
