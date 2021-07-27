import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { LogService } from '../../services/log.service';
import { StatusMessageService } from '../../services/status-message.service';
import { TimezoneService } from '../../services/timezone.service';
import { GeneralLogEntry } from '../../types/api-output';
import { LogsPageComponent } from './logs-page.component';
import { LogsPageModule } from './logs-page.module';
import Spy = jasmine.Spy;

describe('LogsPageComponent', () => {
  let component: LogsPageComponent;
  let fixture: ComponentFixture<LogsPageComponent>;
  let logService: LogService;
  let timezoneService: TimezoneService;
  let statusMessageService: StatusMessageService;

  beforeEach(async(() => {
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
      logsSeverity: 'ERROR',
      logsMinSeverity: '',
      logsEvent: '',
      logsFilter: '',
      logsDateFrom: { year: 2021, month: 6, day: 1 },
      logsTimeFrom: { hour: 23, minute: 59 },
      logsDateTo: { year: 2021, month: 6, day: 2 },
      logsTimeTo: { hour: 23, minute: 59 },
      advancedFilters: {},
    };
    component.isLoading = false;
    component.isSearching = true;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should search for logs when search button is clicked', () => {
    const logSpy: Spy = spyOn(logService, 'searchLogs').and
        .returnValue(of({ logEntries: [] }));
    const timeSpy: Spy = spyOn(timezoneService, 'resolveLocalDateTime').and
        .returnValue(0);

    component.isLoading = false;
    component.isSearching = false;
    component.formModel = {
      logsSeverity: 'INFO',
      logsMinSeverity: '',
      logsEvent: '',
      logsFilter: 'severity',
      logsDateFrom: { year: 2021, month: 6, day: 1 },
      logsTimeFrom: { hour: 23, minute: 59 },
      logsDateTo: { year: 2021, month: 6, day: 2 },
      logsTimeTo: { hour: 23, minute: 59 },
      advancedFilters: {},
    };
    fixture.detectChanges();

    fixture.debugElement.nativeElement.querySelector('#query-button').click();

    expect(timeSpy).toHaveBeenCalledTimes(2);
    expect(logSpy).toHaveBeenCalledWith({
      searchFrom: '0',
      searchUntil: '0',
      order: 'desc',
      severity: 'INFO',
      advancedFilters: {},
    });
  });

  it('should search for logs with minimum severity', () => {
    const logSpy: Spy = spyOn(logService, 'searchLogs').and
        .returnValue(of({ logEntries: [] }));
    const timeSpy: Spy = spyOn(timezoneService, 'resolveLocalDateTime').and
        .returnValue(0);

    component.isLoading = false;
    component.isSearching = false;
    component.formModel = {
      logsSeverity: '',
      logsMinSeverity: 'INFO',
      logsEvent: '',
      logsFilter: 'minSeverity',
      logsDateFrom: { year: 2021, month: 6, day: 1 },
      logsTimeFrom: { hour: 23, minute: 59 },
      logsDateTo: { year: 2021, month: 6, day: 2 },
      logsTimeTo: { hour: 23, minute: 59 },
      advancedFilters: {},
    };
    fixture.detectChanges();

    fixture.debugElement.nativeElement.querySelector('#query-button').click();

    expect(timeSpy).toHaveBeenCalledTimes(2);
    expect(logSpy).toHaveBeenCalledWith({
      searchFrom: '0',
      searchUntil: '0',
      order: 'desc',
      minSeverity: 'INFO',
      advancedFilters: {},
    });
  });

  it('should search for logs with event type', () => {
    const logSpy: Spy = spyOn(logService, 'searchLogs').and
        .returnValue(of({ logEntries: [] }));
    const timeSpy: Spy = spyOn(timezoneService, 'resolveLocalDateTime').and
        .returnValue(0);

    component.isLoading = false;
    component.isSearching = false;
    component.formModel = {
      logsSeverity: '',
      logsMinSeverity: '',
      logsEvent: 'REQUEST_LOG',
      logsFilter: 'event',
      logsDateFrom: { year: 2021, month: 6, day: 1 },
      logsTimeFrom: { hour: 23, minute: 59 },
      logsDateTo: { year: 2021, month: 6, day: 2 },
      logsTimeTo: { hour: 23, minute: 59 },
      advancedFilters: {
        traceId: 'testTrace',
        googleId: 'testGoogleId',
        sourceLocationFile: 'testFile',
        sourceLocationFunction: 'testFunction',
      },
    };
    fixture.detectChanges();

    fixture.debugElement.nativeElement.querySelector('#query-button').click();

    expect(timeSpy).toHaveBeenCalledTimes(2);
    expect(logSpy).toHaveBeenCalledWith({
      searchFrom: '0',
      searchUntil: '0',
      order: 'desc',
      logEvent: 'REQUEST_LOG',
      advancedFilters: {
        traceId: 'testTrace',
        googleId: 'testGoogleId',
        sourceLocationFile: 'testFile',
        sourceLocationFunction: 'testFunction',
      },
    });
  });

  it('should display error message if severity level is not selected', () => {
    component.isLoading = false;
    component.isSearching = false;
    component.formModel = {
      logsSeverity: '',
      logsMinSeverity: '',
      logsEvent: '',
      logsFilter: 'severity',
      logsDateFrom: { year: 2021, month: 6, day: 1 },
      logsTimeFrom: { hour: 23, minute: 59 },
      logsDateTo: { year: 2021, month: 6, day: 2 },
      logsTimeTo: { hour: 23, minute: 59 },
      advancedFilters: {},
    };
    const spy: Spy = spyOn(statusMessageService, 'showErrorToast');
    fixture.detectChanges();
    fixture.debugElement.nativeElement.querySelector('#query-button').click();
    expect(spy).lastCalledWith('Please choose a severity level');
  });

  it('should display error message if minimum severity level is not selected', () => {
    component.isLoading = false;
    component.isSearching = false;
    component.formModel = {
      logsSeverity: '',
      logsMinSeverity: '',
      logsEvent: '',
      logsFilter: 'minSeverity',
      logsDateFrom: { year: 2021, month: 6, day: 1 },
      logsTimeFrom: { hour: 23, minute: 59 },
      logsDateTo: { year: 2021, month: 6, day: 2 },
      logsTimeTo: { hour: 23, minute: 59 },
      advancedFilters: {},
    };
    const spy: Spy = spyOn(statusMessageService, 'showErrorToast');
    fixture.detectChanges();
    fixture.debugElement.nativeElement.querySelector('#query-button').click();
    expect(spy).lastCalledWith('Please choose a minimum severity level');
  });

  it('should display error message if event type is not selected', () => {
    component.isLoading = false;
    component.isSearching = false;
    component.formModel = {
      logsSeverity: '',
      logsMinSeverity: '',
      logsEvent: '',
      logsFilter: 'event',
      logsDateFrom: { year: 2021, month: 6, day: 1 },
      logsTimeFrom: { hour: 23, minute: 59 },
      logsDateTo: { year: 2021, month: 6, day: 2 },
      logsTimeTo: { hour: 23, minute: 59 },
      advancedFilters: {},
    };
    const spy: Spy = spyOn(statusMessageService, 'showErrorToast');
    fixture.detectChanges();
    fixture.debugElement.nativeElement.querySelector('#query-button').click();
    expect(spy).lastCalledWith('Please choose an event type');
  });

  it('should display error message if source function is filled and source file is empty', () => {
    component.isLoading = false;
    component.isSearching = false;
    component.formModel = {
      logsSeverity: 'INFO',
      logsMinSeverity: '',
      logsEvent: '',
      logsFilter: 'severity',
      logsDateFrom: { year: 2021, month: 6, day: 1 },
      logsTimeFrom: { hour: 23, minute: 59 },
      logsDateTo: { year: 2021, month: 6, day: 2 },
      logsTimeTo: { hour: 23, minute: 59 },
      advancedFilters: { sourceLocationFunction: 'testFunction' },
    };
    const spy: Spy = spyOn(statusMessageService, 'showErrorToast');
    fixture.detectChanges();
    fixture.debugElement.nativeElement.querySelector('#query-button').click();
    expect(spy).lastCalledWith('Please fill in Source location file or clear Source location function');
  });

  it('should disable load button if there is no next page token', () => {
    spyOn(logService, 'searchLogs').and.returnValue(of({ logEntries: [] }));
    spyOn(timezoneService, 'resolveLocalDateTime').and.returnValue(0);
    component.formModel = {
      logsSeverity: 'INFO',
      logsMinSeverity: '',
      logsEvent: '',
      logsFilter: 'severity',
      logsDateFrom: { year: 2021, month: 6, day: 1 },
      logsTimeFrom: { hour: 23, minute: 59 },
      logsDateTo: { year: 2021, month: 6, day: 2 },
      logsTimeTo: { hour: 23, minute: 59 },
      advancedFilters: {},
    };
    component.isSearching = false;
    component.hasResult = true;
    component.searchForLogs();
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#load-previous-button');
    expect(button).toBeNull();
  });

  it('should search for all error logs when search button is clicked', () => {
    const logSpy: Spy = spyOn(logService, 'searchLogs').and
        .returnValues(of({ logEntries: [], nextPageToken: 'token' }), of({ logEntries: [] }));
    const timeSpy: Spy = spyOn(timezoneService, 'resolveLocalDateTime').and
        .returnValue(0);

    component.isLoading = false;
    component.isSearching = false;
    component.isTableView = false;

    fixture.detectChanges();
    fixture.debugElement.nativeElement.querySelector('#query-button').click();

    expect(timeSpy).toHaveBeenCalledTimes(2);
    expect(logSpy).toHaveBeenCalledTimes(2);
    expect(logSpy.calls.mostRecent().args).toEqual([{
      searchFrom: '0',
      searchUntil: '0',
      severity: 'ERROR',
      nextPageToken: 'token',
      advancedFilters: {},
    }]);
  });

  it('should sort logs based on source location', () => {
    component.isTableView = false;
    const testLog1: GeneralLogEntry = {
      logName: 'stderr',
      severity: 'ERROR',
      trace: 'testTrace1',
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
      logName: 'stderr',
      severity: 'ERROR',
      trace: 'testTrace2',
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
      logName: 'stderr',
      severity: 'ERROR',
      trace: 'testTrace3',
      resourceIdentifier: {},
      sourceLocation: {
        file: 'file2',
        line: 10,
        function: 'function1',
      },
      timestamp: 1549095330000,
      message: 'message',
    };
    spyOn(logService, 'searchLogs').and
      .returnValue(of({ logEntries: [testLog1, testLog2, testLog3] }));
    spyOn(timezoneService, 'resolveLocalDateTime').and.returnValue(0);

    component.isLoading = false;
    component.isSearching = false;
    component.isTableView = false;
    component.searchForLogs();

    expect(component.histogramResult.length).toEqual(2);
  });

  it('should search for all error logs when search button is clicked', () => {
    const logSpy: Spy = spyOn(logService, 'searchLogs').and
        .returnValues(of({ logEntries: [], nextPageToken: 'token' }), of({ logEntries: [] }));
    const timeSpy: Spy = spyOn(timezoneService, 'resolveLocalDateTime').and
        .returnValue(0);

    component.isLoading = false;
    component.isSearching = false;
    component.isTableView = false;

    fixture.detectChanges();
    fixture.debugElement.nativeElement.querySelector('#query-button').click();

    expect(timeSpy).toHaveBeenCalledTimes(2);
    expect(logSpy).toHaveBeenCalledTimes(2);
    expect(logSpy.calls.mostRecent().args).toEqual([{
      searchFrom: '0',
      searchUntil: '0',
      severity: 'ERROR',
      nextPageToken: 'token',
      advancedFilters: {},
    }]);
  });

  it('should sort logs based on source location', () => {
    component.isTableView = false;
    const testLog1: GeneralLogEntry = {
      logName: 'stderr',
      severity: 'ERROR',
      trace: 'testTrace1',
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
      logName: 'stderr',
      severity: 'ERROR',
      trace: 'testTrace2',
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
      logName: 'stderr',
      severity: 'ERROR',
      trace: 'testTrace3',
      resourceIdentifier: {},
      sourceLocation: {
        file: 'file2',
        line: 10,
        function: 'function1',
      },
      timestamp: 1549095330000,
      message: 'message',
    };
    spyOn(logService, 'searchLogs').and
      .returnValue(of({ logEntries: [testLog1, testLog2, testLog3] }));
    spyOn(timezoneService, 'resolveLocalDateTime').and.returnValue(0);

    component.isLoading = false;
    component.isSearching = false;
    component.isTableView = false;
    component.searchForLogs();

    expect(component.histogramResult.length).toEqual(2);
  });
});
