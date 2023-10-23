import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { GeneralLogEntry, LogEvent, LogSeverity, ExceptionLogDetails } from '../../../../types/api-output';

import { ExceptionLogDetailsComponent } from './exception-log-details.component';

describe('ExceptionLogDetailsComponent', () => {
  let component: ExceptionLogDetailsComponent;
  let fixture: ComponentFixture<ExceptionLogDetailsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ExceptionLogDetailsComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExceptionLogDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create stack trace string', () => {
    const details: ExceptionLogDetails = {
      event: LogEvent.EXCEPTION_LOG,
      exceptionClass: 'exceptionClass1',
      exceptionClasses: ['exceptionClass1'],
      exceptionStackTraces: [['exceptionStackTrace1']],
      exceptionMessages: ['exceptionMessage1'],
      loggerSourceLocation: { file: 'file', line: 1, function: 'function' },
    };

    const stackTrace = component.createStackTraceString(details);
    expect(stackTrace).toContain('exceptionClass1: exceptionMessage1');
    expect(stackTrace).toContain('at exceptionStackTrace');
  });

  // Test for get log
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

  it('should clear details when exceptionStackTrace exists', () => {
    const detailsForParse: ExceptionLogDetails = {
      event: LogEvent.EXCEPTION_LOG,
      exceptionClass: 'exceptionClass',
      exceptionClasses: ['exceptionClass'],
      exceptionStackTraces: [['exceptionStackTrace']],
      exceptionMessages: ['exceptionMessage'],
      loggerSourceLocation: { file: 'log.txt', line: 43, function: 'test_function' },
    };

    const log = {
      severity: LogSeverity.INFO,
      trace: 'test_trace',
      insertId: '1',
      resourceIdentifier: {},
      sourceLocation: { file: 'log.txt', line: 42, function: 'test_function' },
      timestamp: Date.now(),
      details: detailsForParse,
    };
    component.log = log;

    expect(component.details.exceptionClasses).toBeUndefined();
    expect(component.details.exceptionMessages).toBeUndefined();
    expect(component.details.exceptionStackTraces).toBeUndefined();
  });

  it('should return an empty string if exceptionClasses and exceptionStackTraces mismatch', () => {
    const details: ExceptionLogDetails = {
      event: LogEvent.EXCEPTION_LOG,
      exceptionClass: 'exceptionClass',
      exceptionClasses: ['exceptionClass'],
      exceptionStackTraces: [['exceptionStackTrace'], ['another exceptionStackTrace']],
      exceptionMessages: ['exceptionMessage', 'another exceptionMessage'],
      loggerSourceLocation: { file: 'file', line: 1, function: 'function' },
    };

    const stackTrace = component.createStackTraceString(details);
    expect(stackTrace).toBe('');
  });

  it('should return an empty string if exceptionMessages count mismatch with exceptionClasses', () => {
    const details: ExceptionLogDetails = {
      event: LogEvent.EXCEPTION_LOG,
      exceptionClass: 'exceptionClass',
      exceptionClasses: ['exceptionClass'],
      exceptionStackTraces: [['exceptionStackTrace']],
      exceptionMessages: ['exceptionMessage', 'another exceptionMessage'],
      loggerSourceLocation: { file: 'file', line: 1, function: 'function' },
    };

    const stackTrace = component.createStackTraceString(details);
    expect(stackTrace).toBe('');
  });

});
