import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { GeneralLogEntry, LogEvent, LogSeverity, ExceptionLogDetails } from '../../../../types/api-output';
import { ExceptionLogDetailsComponent } from './exception-log-details.component';

describe('ExceptionLogDetailsComponent', () => {
@@ -19,7 +19,109 @@ describe('ExceptionLogDetailsComponent', () => {
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  describe('Component Initialization', () => {
    it('should instantiate the component', () => {
      expect(component).toBeTruthy();
    });
  });

  describe('Stack Trace String Creation', () => {
    // Check the logic in the createStackTraceString function
    let exceptionDetails: ExceptionLogDetails;
    let log: GeneralLogEntry;

    beforeEach(() => {
      exceptionDetails = {
        event: LogEvent.EXCEPTION_LOG,
        exceptionClass: '',
        exceptionClasses: [],
        exceptionStackTraces: [],
        exceptionMessages: [],
        loggerSourceLocation: { file: 'file', line: 1, function: 'function' },
      };
      log = {
        severity: LogSeverity.INFO,
        trace: 'trace_id_123',
        insertId: 'unique_insert_id_456',
        resourceIdentifier: {},
        sourceLocation: { file: 'test_log.txt', line: 123, function: '' },
        timestamp: Date.now(),
        details: exceptionDetails,
      };
    });

    it('should create a valid stack trace string', () => {
      exceptionDetails.exceptionClasses = [''];
      exceptionDetails.exceptionStackTraces = [['']];
      exceptionDetails.exceptionMessages = ['message'];

      component.log = { ...log, details: exceptionDetails };
      expect(component.exceptionStackTrace).toContain(': message');
      expect(component.exceptionStackTrace).toContain('        at ');
    });

    it('should return an empty string if the number of exceptionClasses and exceptionStackTraces is the same', () => {
      exceptionDetails.exceptionClasses = [''];
      exceptionDetails.exceptionStackTraces = [[''], ['']];
      exceptionDetails.exceptionMessages = ['', ''];

      component.log = { ...log, details: exceptionDetails };
      expect(component.exceptionStackTrace).toBe('');
    });

    it('should return an empty string if the number of exceptionClasses and exceptionMessages '
    + 'is different, when the number of exceptionClasses and exceptionStackTraces is the same', () => {
      exceptionDetails.exceptionClasses = [''];
      exceptionDetails.exceptionStackTraces = [['']];
      exceptionDetails.exceptionMessages = ['', ''];

      component.log = { ...log, details: exceptionDetails };
      expect(component.exceptionStackTrace).toBe('');
    });
  });

  describe('Log Entry Manipulation', () => {
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

    it('should clear exception exceptionDetails when exceptionStackTrace exists', () => {
      const exceptionDetailsForParse: ExceptionLogDetails = {
        event: LogEvent.EXCEPTION_LOG,
        exceptionClass: '',
        exceptionClasses: [''],
        exceptionStackTraces: [['']],
        exceptionMessages: [''],
        loggerSourceLocation: { file: 'test_log.txt', line: 43, function: '' },
      };

      const log: GeneralLogEntry = {
        ...exceptionDetailsForParse,
        severity: LogSeverity.INFO,
        trace: 'trace_id_123',
        insertId: 'unique_insert_id_456',
        resourceIdentifier: {},
        sourceLocation: { file: 'test_log.txt', line: 123, function: '' },
        timestamp: Date.now(),
        details: exceptionDetailsForParse,
      };

      component.log = log;

      expect(component.details.exceptionClasses).toBeUndefined();
      expect(component.details.exceptionMessages).toBeUndefined();
      expect(component.details.exceptionStackTraces).toBeUndefined();
    });
  });
});
