import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { LogService } from '../../services/log.service';
import { StatusMessageService } from '../../services/status-message.service';
import { TimezoneService } from '../../services/timezone.service';
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
      imports: [NgbModule, LogsPageModule, HttpClientTestingModule],
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
    };
    component.isLoading = false;
    component.isSearching = true;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should search for logs when search button is clicked', () => {
    const logSpy: Spy = spyOn(logService, 'searchLogs').and
        .returnValue(of({ logEntries: [] }));
    const timeSpy: Spy = spyOn(timezoneService, 'getResolvedTimestamp').and
        .returnValue(of({ timestamp: 0, message: '' }));

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
    };
    fixture.detectChanges();

    fixture.debugElement.nativeElement.querySelector('#query-button').click();

    expect(timeSpy).toHaveBeenCalledTimes(2);
    expect(logSpy).toHaveBeenCalledWith({
      searchFrom: '0',
      searchUntil: '0',
      severity: 'INFO',
    });
  });

  it('should search for logs with minimum severity', () => {
    const logSpy: Spy = spyOn(logService, 'searchLogs').and
        .returnValue(of({ logEntries: [] }));
    const timeSpy: Spy = spyOn(timezoneService, 'getResolvedTimestamp').and
        .returnValue(of({ timestamp: 0, message: '' }));

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
    };
    fixture.detectChanges();

    fixture.debugElement.nativeElement.querySelector('#query-button').click();

    expect(timeSpy).toHaveBeenCalledTimes(2);
    expect(logSpy).toHaveBeenCalledWith({
      searchFrom: '0',
      searchUntil: '0',
      minSeverity: 'INFO',
    });
  });

  it('should search for logs with event type', () => {
    const logSpy: Spy = spyOn(logService, 'searchLogs').and
        .returnValue(of({ logEntries: [] }));
    const timeSpy: Spy = spyOn(timezoneService, 'getResolvedTimestamp').and
        .returnValue(of({ timestamp: 0, message: '' }));

    component.isLoading = false;
    component.isSearching = false;
    component.formModel = {
      logsSeverity: '',
      logsMinSeverity: '',
      logsEvent: 'REQUEST_RECEIVED',
      logsFilter: 'event',
      traceId: 'testTrace',
      googleId: 'testGoogleId',
      sourceLocationFile: 'testFile',
      sourceLocationFunction: 'testFunction',
      logsDateFrom: { year: 2021, month: 6, day: 1 },
      logsTimeFrom: { hour: 23, minute: 59 },
      logsDateTo: { year: 2021, month: 6, day: 2 },
      logsTimeTo: { hour: 23, minute: 59 },
    };
    fixture.detectChanges();

    fixture.debugElement.nativeElement.querySelector('#query-button').click();

    expect(timeSpy).toHaveBeenCalledTimes(2);
    expect(logSpy).toHaveBeenCalledWith({
      searchFrom: '0',
      searchUntil: '0',
      logEvent: 'REQUEST_RECEIVED',
      traceId: 'testTrace',
      googleId: 'testGoogleId',
      sourceLocationFile: 'testFile',
      sourceLocationFunction: 'testFunction',
    });
  });

  it('should display error messgae if severity level is not selected', () => {
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
    };
    const spy: Spy = spyOn(statusMessageService, 'showErrorToast');
    fixture.detectChanges();
    fixture.debugElement.nativeElement.querySelector('#query-button').click();
    expect(spy).lastCalledWith('Please choose a severity level');
  });

  it('should display error messgae if minimum severity level is not selected', () => {
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
    };
    const spy: Spy = spyOn(statusMessageService, 'showErrorToast');
    fixture.detectChanges();
    fixture.debugElement.nativeElement.querySelector('#query-button').click();
    expect(spy).lastCalledWith('Please choose a minimum severity level');
  });

  it('should display error messgae if event type is not selected', () => {
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
    };
    const spy: Spy = spyOn(statusMessageService, 'showErrorToast');
    fixture.detectChanges();
    fixture.debugElement.nativeElement.querySelector('#query-button').click();
    expect(spy).lastCalledWith('Please choose an event type');
  });

  it('should display error messgae if source function is filled and source file is empty', () => {
    component.isLoading = false;
    component.isSearching = false;
    component.formModel = {
      logsSeverity: 'INFO',
      logsMinSeverity: '',
      logsEvent: '',
      logsFilter: 'severity',
      sourceLocationFile: '',
      sourceLocationFunction: 'testFunction',
      logsDateFrom: { year: 2021, month: 6, day: 1 },
      logsTimeFrom: { hour: 23, minute: 59 },
      logsDateTo: { year: 2021, month: 6, day: 2 },
      logsTimeTo: { hour: 23, minute: 59 },
    };
    const spy: Spy = spyOn(statusMessageService, 'showErrorToast');
    fixture.detectChanges();
    fixture.debugElement.nativeElement.querySelector('#query-button').click();
    expect(spy).lastCalledWith('Please fill in Source location file or clear Source location function');
  });

  it('should disable load button if there is no next page token', () => {
    component.nextPageToken = '';
    component.isSearching = false;
    component.hasResult = true;
    fixture.detectChanges();
    const button: any = fixture.debugElement.nativeElement.querySelector('#load-button');
    expect(button.disabled).toBeTruthy();
  });
});
