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

  const initialLogDetails: ExceptionLogDetails = {
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
  const expectedLogDetails = {
    ...initialLogDetails,
    exceptionClasses: undefined,
    exceptionMessages: undefined,
    exceptionStackTraces: undefined,
  };
  const expectedLogValue: GeneralLogEntry = {
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
    details: initialLogDetails,
  };
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

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ExceptionLogDetailsComponent],
    }).compileComponents();
  }));

  beforeEach(waitForAsync(() => {
    fixture = TestBed.createComponent(ExceptionLogDetailsComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('log', expectedLogValue);
    fixture.detectChanges();
    fixture.whenStable();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have a properly formatted stack trace string', () => {
    expect(component.exceptionStackTrace).toEqual(
      expectedExceptionStackTraceString,
    );
  });

  it('should remove exception classes, messages, and stack traces from log details', () => {
    expect(component.details).toEqual(expectedLogDetails);
  });
});
