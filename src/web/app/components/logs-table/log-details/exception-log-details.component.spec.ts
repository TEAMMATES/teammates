import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ExceptionLogDetailsComponent } from './exception-log-details.component';
import {
  ExceptionLogDetails,
  GeneralLogEntry,
  LogEvent,
  LogSeverity,
} from '../../../../types/api-output';

describe('ExceptionLogDetailsComponent', () => {
  let component: ExceptionLogDetailsComponent;
  let fixture: ComponentFixture<ExceptionLogDetailsComponent>;

  const baseInitialLogDetails: ExceptionLogDetails = {
    event: LogEvent.EXCEPTION_LOG,
    message: 'Test exception log details message.',
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
  const baseExpectedLogDetails = {
    ...baseInitialLogDetails,
    exceptionClasses: undefined,
    exceptionMessages: undefined,
    exceptionStackTraces: undefined,
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
      file: 'com.mock.Mock',
      line: 100,
      function: 'run',
    },
    timestamp: 1000,
    message: 'Test exception log message',
    details: baseInitialLogDetails,
  };
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

    expect(component.logValue).toEqual(baseExpectedLogValue);
  });

  describe('input log is a valid exception log', () => {
    beforeEach(() => {
      fixture.componentRef.setInput('log', baseExpectedLogValue);
      fixture.detectChanges();
    });

    it('should have a properly formatted stack trace string', () => {
      expect(component.exceptionStackTrace).toBe(
        baseExpectedExceptionStackTraceString,
      );
    });

    it('should extract exception details', () => {
      expect(component.details).toEqual(baseExpectedLogDetails);
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
    const expectedLogDetail: ExceptionLogDetails = {
      ...baseInitialLogDetails,
      exceptionClasses: baseInitialLogDetails.exceptionClasses.slice(1),
    };
    const logEntryWithMissingExceptionClass: GeneralLogEntry = {
      ...baseExpectedLogValue,
      details: expectedLogDetail,
    };

    beforeEach(() => {
      fixture.componentRef.setInput('log', logEntryWithMissingExceptionClass);
      fixture.detectChanges();
    });

    it('should have an empty exception stack trace', () => {
      expect(component.exceptionStackTrace).toBe('');
    });

    it('should preserve exception details in the log details', () => {
      expect(component.details).toEqual(expectedLogDetail);
    });
  });

  describe('input log is an invalid exception log with wrong number of exception messages', () => {
    const expectedLogDetail: ExceptionLogDetails = {
      ...baseInitialLogDetails,
      exceptionMessages: baseInitialLogDetails.exceptionMessages!.slice(1),
    };
    const logEntryWithMissingExceptionMessage: GeneralLogEntry = {
      ...baseExpectedLogValue,
      details: expectedLogDetail,
    };

    beforeEach(() => {
      fixture.componentRef.setInput('log', logEntryWithMissingExceptionMessage);
      fixture.detectChanges();
    });

    it('should have an empty exceptionStackTrace', () => {
      expect(component.exceptionStackTrace).toBe('');
    });

    it('should not remove exception details from the log details', () => {
      expect(component.details).toEqual(expectedLogDetail);
    });
  });
});
