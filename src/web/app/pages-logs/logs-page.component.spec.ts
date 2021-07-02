import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { LogService } from '../../services/log.service';
import { TimezoneService } from '../../services/timezone.service';
import { GeneralLogEntry, Type } from '../../types/api-output';
import { LogsPageComponent } from './logs-page.component';
import { LogsPageModule } from './logs-page.module';
import Spy = jasmine.Spy;

describe('LogsPageComponent', () => {
  let component: LogsPageComponent;
  let fixture: ComponentFixture<LogsPageComponent>;
  let logService: LogService;
  let timezoneService: TimezoneService;

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
    const logSeverity: Set<string> = new Set();
    logSeverity.add('ERROR');
    component.formModel = {
      logsSeverity: logSeverity,
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
    const testLog: GeneralLogEntry = {
      logName: 'stdout',
      severity: 'INFO',
      trace: 'testTrace',
      sourceLocation: {
        file: 'file',
        line: 10,
        function: 'function',
      },
      payload: {
        type: Type.STRING,
        data: 'textPayload message',
      },
      timestamp: 1549095330000,
    };
    const logSpy: Spy = spyOn(logService, 'searchLogs').and
        .returnValue(of({ logEntries: [testLog], nextPageToken: 'testToken' }));
    const timeSpy: Spy = spyOn(timezoneService, 'getResolvedTimestamp').and
        .returnValue(of({ timestamp: 0, message: '' }));

    component.isLoading = false;
    component.isSearching = false;
    const logSeverity: Set<string> = new Set();
    logSeverity.add('INFO');
    component.formModel = {
      logsSeverity: logSeverity,
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
      severities: 'INFO',
    });

    expect(component.searchResults.length).toEqual(1);
  });

  it('should disable previous button if at first page', () => {
    component.currentPageNumber = 0;
    fixture.detectChanges();
    const button: any = fixture.debugElement.nativeElement.querySelector('#previous-button');
    expect(button.disabled).toBeTruthy();
  });

  it('should disable next button if there is no next page token', () => {
    component.nextPageToken = '';
    fixture.detectChanges();
    const button: any = fixture.debugElement.nativeElement.querySelector('#next-button');
    expect(button.disabled).toBeTruthy();
  });
});
