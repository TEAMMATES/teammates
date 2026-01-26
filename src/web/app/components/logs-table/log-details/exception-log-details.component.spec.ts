import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ExceptionLogDetailsComponent } from './exception-log-details.component';
import { generalLogEntryBuilder } from '../../../../test-helpers/log-test-helpers';
import {
  ExceptionLogDetails,
  GeneralLogEntry,
  LogEvent,
} from '../../../../types/api-output';

describe('ExceptionLogDetailsComponent', () => {
  let component: ExceptionLogDetailsComponent;
  let fixture: ComponentFixture<ExceptionLogDetailsComponent>;

  const baseInitialLogDetails: ExceptionLogDetails = {
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
  const baseExpectedLogValue: GeneralLogEntry = generalLogEntryBuilder()
    .details(baseInitialLogDetails)
    .message('Test exception log message')
    .sourceLocation({
      file: 'com.mock.Mock',
      line: 100,
      function: 'run',
    })
    .build();
  const baseExpectedExceptionStackTraceString =
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

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ExceptionLogDetailsComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(ExceptionLogDetailsComponent);
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

  describe('input log is a valid exception log', () => {
    beforeEach(() => {
      fixture.componentRef.setInput('log', baseExpectedLogValue);
      fixture.detectChanges();
    });

    it('should store basic exception details', () => {
      expect(component.details.event).toBe(LogEvent.EXCEPTION_LOG);
      expect(component.details.exceptionClass).toBe('MockException');
      expect(component.details.message).toBe(baseInitialLogDetails.message);
    });

    it('should have a properly formatted stack trace string', () => {
      expect(component.exceptionStackTrace).toBe(
        baseExpectedExceptionStackTraceString,
      );
    });

    it('should remove exception classes, messages, and stack traces from details', () => {
      expect(component.details.exceptionClasses).toBeUndefined();
      expect(component.details.exceptionMessages).toBeUndefined();
      expect(component.details.exceptionStackTraces).toBeUndefined();
    });

  });

  describe('input log is not an exception log', () => {
    const expectedLogValue: GeneralLogEntry = {
      ...baseExpectedLogValue,
      details: {
        event: LogEvent.DEFAULT_LOG,
        message: 'Test default log details message',
      },
    };

    beforeEach(() => {
      fixture.componentRef.setInput('log', expectedLogValue);
      fixture.detectChanges();
    });

    it('should not extract exception details', () => {
      expect(component.details).toBeUndefined();
      expect(component.exceptionStackTrace).toBeUndefined();
    });
  });

  describe('input log is an invalid exception log with wrong number of exception classes', () => {
    const expectedLogDetails: ExceptionLogDetails = {
      ...baseInitialLogDetails,
      exceptionClasses: baseInitialLogDetails.exceptionClasses.slice(1),
    };
    const logEntryWithMissingExceptionClass: GeneralLogEntry = {
      ...baseExpectedLogValue,
      details: expectedLogDetails,
    };

    beforeEach(() => {
      fixture.componentRef.setInput('log', logEntryWithMissingExceptionClass);
      fixture.detectChanges();
    });

    it('should have an empty exception stack trace string', () => {
      expect(component.exceptionStackTrace).toBe('');
    });

    it('should preserve all exception details in the log details', () => {
      expect(component.details.exceptionClasses).toEqual(expectedLogDetails.exceptionClasses);
      expect(component.details.exceptionMessages).toEqual(expectedLogDetails.exceptionMessages);
      expect(component.details.exceptionStackTraces).toEqual(expectedLogDetails.exceptionStackTraces);
    });
  });

  describe('input log is an invalid exception log with wrong number of exception messages', () => {
    const expectedLogDetails: ExceptionLogDetails = {
      ...baseInitialLogDetails,
      exceptionMessages: baseInitialLogDetails.exceptionMessages!.slice(1),
    };
    const logEntryWithMissingExceptionMessage: GeneralLogEntry = {
      ...baseExpectedLogValue,
      details: expectedLogDetails,
    };

    beforeEach(() => {
      fixture.componentRef.setInput('log', logEntryWithMissingExceptionMessage);
      fixture.detectChanges();
    });

    it('should have an empty exception stack trace string', () => {
      expect(component.exceptionStackTrace).toBe('');
    });

    it('should preserve all exception details in the log details', () => {
      expect(component.details.exceptionClasses).toEqual(expectedLogDetails.exceptionClasses);
      expect(component.details.exceptionMessages).toEqual(expectedLogDetails.exceptionMessages);
      expect(component.details.exceptionStackTraces).toEqual(expectedLogDetails.exceptionStackTraces);
    });
  });
});
