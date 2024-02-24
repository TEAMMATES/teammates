import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import { LogsPageComponent } from './logs-page.component';
import { LogsPageModule } from './logs-page.module';
import { LogService } from '../../../services/log.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { GeneralLogEntry, LogEvent, LogSeverity } from '../../../types/api-output';
import { getLatestTimeFormat } from '../../../types/datetime-const';

describe('LogsPageComponent', () => {
  let component: LogsPageComponent;
  let fixture: ComponentFixture<LogsPageComponent>;
  let logService: LogService;
  let timezoneService: TimezoneService;
  let statusMessageService: StatusMessageService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [NgbModule, LogsPageModule, HttpClientTestingModule, RouterTestingModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LogsPageComponent);
    logService = TestBed.inject(LogService);
    timezoneService = TestBed.inject(TimezoneService);
    statusMessageService = TestBed.inject(StatusMessageService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when page is still loading', () => {
    component.isLoading = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when searching for details in search form', () => {
    component.formModel = {
      logsDateFrom: { year: 2021, month: 6, day: 1 },
      logsTimeFrom: getLatestTimeFormat(),
      logsDateTo: { year: 2021, month: 6, day: 2 },
      logsTimeTo: getLatestTimeFormat(),
      filters: {
        severity: LogSeverity.ERROR,
        minSeverity: LogSeverity.INFO,
        logEvent: LogEvent.REQUEST_LOG,
      },
    };
    component.filterType = 'EVENT';
    component.isLoading = false;
    component.isSearching = true;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should search for logs when search button is clicked', () => {
    const logSpy: SpyInstance = jest.spyOn(logService, 'searchLogs')
        .mockReturnValue(of({ logEntries: [], hasNextPage: false }));
    const timeSpy: SpyInstance = jest.spyOn(timezoneService, 'resolveLocalDateTime')
        .mockReturnValue(0);

    component.isLoading = false;
    component.isSearching = false;
    component.filterType = 'SEVERITY';
    component.formModel = {
      logsDateFrom: { year: 2021, month: 6, day: 1 },
      logsTimeFrom: getLatestTimeFormat(),
      logsDateTo: { year: 2021, month: 6, day: 2 },
      logsTimeTo: getLatestTimeFormat(),
      filters: {
        severity: LogSeverity.INFO,
        minSeverity: LogSeverity.INFO,
        logEvent: LogEvent.REQUEST_LOG,
      },
    };
    fixture.detectChanges();

    fixture.debugElement.nativeElement.querySelector('#query-button').click();

    expect(timeSpy).toHaveBeenCalledTimes(2);
    expect(logSpy).toHaveBeenCalledWith({
      startTime: 0,
      endTime: 0,
      order: 'desc',
      severity: LogSeverity.INFO,
      minSeverity: undefined,
      logEvent: undefined,
    });
  });

  it('should search for logs with minimum severity', () => {
    const logSpy: SpyInstance = jest.spyOn(logService, 'searchLogs')
      .mockReturnValue(of({ logEntries: [], hasNextPage: false }));
    const timeSpy: SpyInstance = jest.spyOn(timezoneService, 'resolveLocalDateTime')
      .mockReturnValue(0);

    component.isLoading = false;
    component.isSearching = false;
    component.filterType = 'MIN_SEVERITY';
    component.formModel = {
      logsDateFrom: { year: 2021, month: 6, day: 1 },
      logsTimeFrom: getLatestTimeFormat(),
      logsDateTo: { year: 2021, month: 6, day: 2 },
      logsTimeTo: getLatestTimeFormat(),
      filters: {
        severity: LogSeverity.INFO,
        minSeverity: LogSeverity.INFO,
        logEvent: LogEvent.REQUEST_LOG,
      },
    };
    fixture.detectChanges();

    fixture.debugElement.nativeElement.querySelector('#query-button').click();

    expect(timeSpy).toHaveBeenCalledTimes(2);
    expect(logSpy).toHaveBeenCalledWith({
      startTime: 0,
      endTime: 0,
      order: 'desc',
      severity: undefined,
      minSeverity: LogSeverity.INFO,
      logEvent: undefined,
    });
  });

  it('should search for logs with event type', () => {
    const logSpy: SpyInstance = jest.spyOn(logService, 'searchLogs')
        .mockReturnValue(of({ logEntries: [], hasNextPage: false }));
    const timeSpy: SpyInstance = jest.spyOn(timezoneService, 'resolveLocalDateTime')
        .mockReturnValue(0);

    component.isLoading = false;
    component.isSearching = false;
    component.filterType = 'EVENT';
    component.formModel = {
      logsDateFrom: { year: 2021, month: 6, day: 1 },
      logsTimeFrom: getLatestTimeFormat(),
      logsDateTo: { year: 2021, month: 6, day: 2 },
      logsTimeTo: getLatestTimeFormat(),
      filters: {
        severity: LogSeverity.INFO,
        minSeverity: LogSeverity.INFO,
        logEvent: LogEvent.REQUEST_LOG,
        traceId: 'testTrace',
        sourceLocation: {
          file: 'testFile',
          line: 0,
          function: 'testFunction',
        },
        userInfoParams: {
          googleId: 'testGoogleId',
          regkey: '',
          email: '',
        },
      },
    };
    fixture.detectChanges();

    fixture.debugElement.nativeElement.querySelector('#query-button').click();

    expect(timeSpy).toHaveBeenCalledTimes(2);
    expect(logSpy).toHaveBeenCalledWith({
      startTime: 0,
      endTime: 0,
      order: 'desc',
      severity: undefined,
      minSeverity: undefined,
      logEvent: LogEvent.REQUEST_LOG,
      traceId: 'testTrace',
      sourceLocation: {
        file: 'testFile',
        line: 0,
        function: 'testFunction',
      },
      userInfoParams: {
        googleId: 'testGoogleId',
        regkey: '',
        email: '',
      },
    });
  });

  it('should display error message if source function is filled and source file is empty', () => {
    component.isLoading = false;
    component.isSearching = false;
    component.filterType = 'SEVERITY';
    component.formModel = {
      logsDateFrom: { year: 2021, month: 6, day: 1 },
      logsTimeFrom: getLatestTimeFormat(),
      logsDateTo: { year: 2021, month: 6, day: 2 },
      logsTimeTo: getLatestTimeFormat(),
      filters: {
        severity: LogSeverity.INFO,
        minSeverity: LogSeverity.INFO,
        logEvent: LogEvent.REQUEST_LOG,
        sourceLocation: {
          file: '',
          line: 0,
          function: 'testFunction',
        },
      },
    };
    const spy: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast');
    fixture.detectChanges();
    fixture.debugElement.nativeElement.querySelector('#query-button').click();
    expect(spy).toHaveBeenLastCalledWith('Please fill in Source location file or clear Source location function');
  });

  it('should disable load button if there is no next page', () => {
    jest.spyOn(logService, 'searchLogs').mockReturnValue(of({ logEntries: [], hasNextPage: false }));
    jest.spyOn(timezoneService, 'resolveLocalDateTime').mockReturnValue(0);
    component.formModel = {
      logsDateFrom: { year: 2021, month: 6, day: 1 },
      logsTimeFrom: getLatestTimeFormat(),
      logsDateTo: { year: 2021, month: 6, day: 2 },
      logsTimeTo: getLatestTimeFormat(),
      filters: {
        severity: LogSeverity.INFO,
        minSeverity: LogSeverity.INFO,
        logEvent: LogEvent.REQUEST_LOG,
      },
    };
    component.filterType = 'SEVERITY';
    component.isSearching = false;
    component.hasResult = true;
    component.searchForLogs();
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#load-previous-button');
    expect(button).toBeNull();
  });

  it('should search for all error logs when search button is clicked', () => {
    const testLog1: GeneralLogEntry = {
      severity: LogSeverity.ERROR,
      trace: 'testTrace1',
      insertId: 'testInsertId1',
      resourceIdentifier: {},
      sourceLocation: {
        file: 'file1',
        line: 10,
        function: 'function1',
      },
      timestamp: 1549095330000,
      message: 'message',
    };
    const logSpy: SpyInstance = jest.spyOn(logService, 'searchLogs')
        .mockReturnValueOnce(of({ logEntries: [testLog1], hasNextPage: true }))
        .mockReturnValueOnce(of({ logEntries: [], hasNextPage: false }));
    const timeSpy: SpyInstance = jest.spyOn(timezoneService, 'resolveLocalDateTime')
        .mockReturnValue(0);

    component.isLoading = false;
    component.isSearching = false;
    component.isTableView = false;

    fixture.detectChanges();
    fixture.debugElement.nativeElement.querySelector('#query-button').click();

    expect(timeSpy).toHaveBeenCalledTimes(2);
    expect(logSpy).toHaveBeenCalledTimes(2);
    expect(logSpy).toHaveBeenLastCalledWith({
      startTime: 0,
      endTime: 1549095330000,
      order: 'desc',
      severity: 'ERROR',
    });
  });

  it('should sort logs based on source location', () => {
    component.isTableView = false;
    const testLog1: GeneralLogEntry = {
      severity: LogSeverity.ERROR,
      trace: 'testTrace1',
      insertId: 'testInsertId1',
      resourceIdentifier: {},
      sourceLocation: {
        file: 'file1',
        line: 10,
        function: 'function1',
      },
      timestamp: 1549095330000,
      message: 'message',
    };
    const testLog2: GeneralLogEntry = {
      severity: LogSeverity.ERROR,
      trace: 'testTrace2',
      insertId: 'testInsertId2',
      resourceIdentifier: {},
      sourceLocation: {
        file: 'file2',
        line: 10,
        function: 'function1',
      },
      timestamp: 1549095330000,
      message: 'message',
    };
    const testLog3: GeneralLogEntry = {
      severity: LogSeverity.ERROR,
      trace: 'testTrace3',
      insertId: 'testInsertId3',
      resourceIdentifier: {},
      sourceLocation: {
        file: 'file2',
        line: 10,
        function: 'function1',
      },
      timestamp: 1549095330000,
      message: 'message',
    };
    jest.spyOn(logService, 'searchLogs')
      .mockReturnValue(of({ logEntries: [testLog1, testLog2, testLog3], hasNextPage: false }));
    jest.spyOn(timezoneService, 'resolveLocalDateTime').mockReturnValue(0);

    component.isLoading = false;
    component.isSearching = false;
    component.isTableView = false;
    component.searchForLogs();

    expect(component.histogramResult.length).toEqual(2);
  });
});
