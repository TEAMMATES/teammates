import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { GeneralLogEntry, LogEvent, LogSeverity, RequestLogDetails } from '../../../../types/api-output';
import { RequestLogDetailsComponent } from './request-log-details.component';

describe('RequestLogDetailsComponent', () => {
  let component: RequestLogDetailsComponent;
  let fixture: ComponentFixture<RequestLogDetailsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RequestLogDetailsComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestLogDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set log and details when input is provided', () => {
    const log = {
      severity: LogSeverity.INFO,
      trace: 'sample-trace',
      insertId: 'sample-insert-id',
      resourceIdentifier: { resource: 'sample' },
      sourceLocation: { file: 'sample-file', line: 1, function: 'sample-function' },
      timestamp: Date.now(),
      details: {
        event: LogEvent.REQUEST_LOG,
        responseStatus: 200,
        responseTime: 100,
        requestMethod: 'GET',
        requestUrl: '/sample/url',
        userAgent: 'Sample User Agent',
        instanceId: 'sample-instance-id',
        webVersion: '1.0.0',
        referrer: 'sample-referrer',
        requestParams: { param1: 'value1', param2: 'value2' },
        requestHeaders: { header1: 'value1', header2: 'value2' },
        requestBody: '{}', // JSON
        actionClass: 'SampleActionClass',
        userInfo: { userId: 'sample-user-id', username: 'sample-username' },
      },
    };

    component.log = log;

    // Act set details.userInfo = undefined;
    const details: RequestLogDetails = JSON.parse(JSON.stringify(log.details)) as RequestLogDetails;
    component.userInfo = details.userInfo;
    details.userInfo = undefined;
    component.details = details;

    expect(component.logValue).toEqual(log);
    expect(component.details).toEqual({
      ...log
      userInfo: undefined, // Ensure userInfo is cleared
    });
    expect(component.userInfo).toEqual(log.details.userInfo);
    expect(component.requestBody).toEqual({});
  });

  it('should not set log and details when log event is not REQUEST_LOG', () => {
    const log = {
      severity: LogSeverity.INFO,
      trace: 'sample-trace',
      insertId: 'sample-insert-id',
      resourceIdentifier: { resource: 'sample' },
      sourceLocation: { file: 'sample-file', line: 1, function: 'sample-function' },
      timestamp: Date.now(),
      details: {
        event: LogEvent.EXCEPTION_LOG, // Not a REQUEST_LOG event
      },
    };

    component.log = log;

    expect(component.logValue).toEqual(log);
    expect(component.details).toBeUndefined();
    expect(component.userInfo).toBeUndefined();
    expect(component.requestBody).toBeUndefined();
  });

  it('should emit the user info when addUserInfoToFilter is called', () => {
    const emitSpy = jest.spyOn(component.addUserInfoEvent, 'emit');
    const userInfo = { regkey: '', email: '', googleId: '' };
    component.addUserInfoToFilter(userInfo);
    expect(emitSpy).toHaveBeenCalledWith(userInfo);
    emitSpy.mockRestore();
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

});
