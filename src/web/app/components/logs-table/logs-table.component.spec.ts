import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { LogDetailsModule } from './log-details/log-details.module';
import { LogLineModule } from './log-line/log-line.module';
import { LogsTableRowModel } from './logs-table-model';
import { LogsTableComponent } from './logs-table.component';
import { LogSeverity, LogEvent, SourceLocation, RequestLogUser } from '../../../types/api-output';

describe('LogsTableComponent', () => {
  let component: LogsTableComponent;
  let fixture: ComponentFixture<LogsTableComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [LogsTableComponent],
      imports: [LogLineModule, LogDetailsModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LogsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should toggle the isDetailsExpanded property of LogsTableRowModel', () => {
    const exampleLog: LogsTableRowModel = {
      logEntry: {
        severity: LogSeverity.INFO,
        trace: 'sample-trace',
        insertId: '123456',
        resourceIdentifier: { key: 'sample-resource' },
        sourceLocation: { file: 'file.ts', line: 123, function: 'someFunctionName' },
        timestamp: 1697635200000,
        message: 'Sample log message',
        details: { event: LogEvent.REQUEST_LOG },
      },
      timestampForDisplay: '2024-10-18T10:00:00Z',
      traceIdForDisplay: 'TRACE1234',
      isDetailsExpanded: false,
    };

    // Expand details
    component.expandDetails(exampleLog);
    expect(exampleLog.isDetailsExpanded).toBe(true);

    // Collapse details
    component.expandDetails(exampleLog);
    expect(exampleLog.isDetailsExpanded).toBe(false);
  });

  it('should return the correct class for severity levels', () => {
    expect(component.getClassForSeverity('INFO')).toBe('info-row');
    expect(component.getClassForSeverity('WARNING')).toBe('warning-row');
    expect(component.getClassForSeverity('ERROR')).toBe('error-row');
    expect(component.getClassForSeverity('DEBUG')).toBe('');
  });

  it('should emit addTraceEvent when addTraceToFilter is called', () => {
    const spy = jest.spyOn(component.addTraceEvent, 'emit');
    const trace = 'sample-trace';

    component.addTraceToFilter(trace);
    expect(spy).toHaveBeenCalledWith(trace);
  });

  it('should emit addActionClassEvent when addActionClassToFilter is called', () => {
    const spy = jest.spyOn(component.addActionClassEvent, 'emit');
    const actionClass = 'sample-action-class';

    component.addActionClassToFilter(actionClass);
    expect(spy).toHaveBeenCalledWith(actionClass);
  });

  it('should emit addExceptionClassEvent when addExceptionClassToFilter is called', () => {
    const spy = jest.spyOn(component.addExceptionClassEvent, 'emit');
    const exceptionClass = 'sample-exception-class';

    component.addExceptionClassToFilter(exceptionClass);
    expect(spy).toHaveBeenCalledWith(exceptionClass);
  });

  it('should emit addSourceLocationEvent when addSourceLocationToFilter is called', () => {
    const spy = jest.spyOn(component.addSourceLocationEvent, 'emit');
    const sourceLocation: SourceLocation = { file: 'file.ts', line: 123, function: 'someFunctionName' };

    component.addSourceLocationToFilter(sourceLocation);
    expect(spy).toHaveBeenCalledWith(sourceLocation);
  });

  it('should emit addUserInfoEvent when addUserInfoToFilter is called', () => {
    const spy = jest.spyOn(component.addUserInfoEvent, 'emit');
    const userInfo: RequestLogUser = { regkey: 'reg123', email: 'user@example.com', googleId: 'google123' };

    component.addUserInfoToFilter(userInfo);
    expect(spy).toHaveBeenCalledWith(userInfo);
  });
});
