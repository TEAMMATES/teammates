import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LogsTableRowModel } from './logs-table-model';
import { LogsTableComponent } from './logs-table.component';
import { LogEvent, RequestLogUser, SourceLocation } from '../../../types/api-output';

describe('LogsTableComponent', () => {
  let component: LogsTableComponent;
  let fixture: ComponentFixture<LogsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LogsTableComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(LogsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('initial state', () => {
    it('should initialize logs as an empty array', () => {
      expect(component.logs).toEqual([]);
    });

    it('should initialize isAdmin as false', () => {
      expect(component.isAdmin).toBe(false);
    });

    it('should expose LogEvent on component', () => {
      expect(component.LogEvent).toBe(LogEvent);
    });
  });

  describe('expandDetails', () => {
    const createRow = (isDetailsExpanded: boolean): LogsTableRowModel => ({
      logEntry: {
        severity: 'INFO',
        trace: 'trace-1',
      } as any,
      timestampForDisplay: '2025-01-01T00:00:00Z',
      traceIdForDisplay: 'trace-1',
      isDetailsExpanded,
    });

    it('should set isDetailsExpanded to true when initially false', () => {
      const row = createRow(false);

      component.expandDetails(row);

      expect(row.isDetailsExpanded).toBe(true);
    });

    it('should set isDetailsExpanded to false when initially true', () => {
      const row = createRow(true);

      component.expandDetails(row);

      expect(row.isDetailsExpanded).toBe(false);
    });

    it('should toggle back to original value when called twice', () => {
      const row = createRow(false);

      component.expandDetails(row);
      component.expandDetails(row);

      expect(row.isDetailsExpanded).toBe(false);
    });
  });

  describe('getClassForSeverity', () => {
    it('should return info-row for INFO', () => {
      expect(component.getClassForSeverity('INFO')).toBe('info-row');
    });

    it('should return warning-row for WARNING', () => {
      expect(component.getClassForSeverity('WARNING')).toBe('warning-row');
    });

    it('should return error-row for ERROR', () => {
      expect(component.getClassForSeverity('ERROR')).toBe('error-row');
    });

    it('should return empty string for unknown severity', () => {
      expect(component.getClassForSeverity('DEBUG')).toBe('');
    });
  });

  describe('event emitters', () => {
    it('should emit addTraceEvent with provided trace', () => {
      const trace = 'trace-abc';
      const emitSpy = jest.spyOn(component.addTraceEvent, 'emit');

      component.addTraceToFilter(trace);

      expect(emitSpy).toHaveBeenCalledTimes(1);
      expect(emitSpy).toHaveBeenCalledWith(trace);
    });

    it('should emit addActionClassEvent with provided action class', () => {
      const actionClass = 'teammates.ui.webapi.GetLogsAction';
      const emitSpy = jest.spyOn(component.addActionClassEvent, 'emit');

      component.addActionClassToFilter(actionClass);

      expect(emitSpy).toHaveBeenCalledTimes(1);
      expect(emitSpy).toHaveBeenCalledWith(actionClass);
    });

    it('should emit addExceptionClassEvent with provided exception class', () => {
      const exceptionClass = 'java.lang.IllegalArgumentException';
      const emitSpy = jest.spyOn(component.addExceptionClassEvent, 'emit');

      component.addExceptionClassToFilter(exceptionClass);

      expect(emitSpy).toHaveBeenCalledTimes(1);
      expect(emitSpy).toHaveBeenCalledWith(exceptionClass);
    });

    it('should emit addSourceLocationEvent with provided source location', () => {
      const sourceLocation: SourceLocation = {
        fileName: 'LogsAction.java',
        lineNumber: 42,
        className: 'teammates.ui.webapi.LogsAction',
        methodName: 'execute',
      } as any;
      const emitSpy = jest.spyOn(component.addSourceLocationEvent, 'emit');

      component.addSourceLocationToFilter(sourceLocation);

      expect(emitSpy).toHaveBeenCalledTimes(1);
      expect(emitSpy).toHaveBeenCalledWith(sourceLocation);
    });

    it('should emit addUserInfoEvent with provided user info', () => {
      const userInfo: RequestLogUser = {
        accountId: '00000000-0000-4000-8000-0000000000a1',
        email: 'instructor@teammates.tmt',
      } as any;
      const emitSpy = jest.spyOn(component.addUserInfoEvent, 'emit');

      component.addUserInfoToFilter(userInfo);

      expect(emitSpy).toHaveBeenCalledTimes(1);
      expect(emitSpy).toHaveBeenCalledWith(userInfo);
    });
  });
});
