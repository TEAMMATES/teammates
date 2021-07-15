import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { LogService } from '../../../services/log.service';
import { TimezoneService } from '../../../services/timezone.service';
import { GeneralLogEntry } from '../../../types/api-output';
import { LogsHistogramPageComponent } from './logs-histogram-page.component';
import { LogsHistogramPageModule } from './logs-histogram-page.module';
import Spy = jasmine.Spy;

describe('LogsHistogramPageComponent', () => {
  let component: LogsHistogramPageComponent;
  let fixture: ComponentFixture<LogsHistogramPageComponent>;
  let logService: LogService;
  let timezoneService: TimezoneService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [NgbModule, LogsHistogramPageModule, HttpClientTestingModule, RouterTestingModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LogsHistogramPageComponent);
    component = fixture.componentInstance;
    logService = TestBed.inject(LogService);
    timezoneService = TestBed.inject(TimezoneService);
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
      logsTimeFrom: { hour: 23, minute: 59 },
      logsDateTo: { year: 2021, month: 6, day: 2 },
      logsTimeTo: { hour: 23, minute: 59 },
    };
    component.isLoading = false;
    component.isSearching = true;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should search for all logs when search button is clicked', () => {
    const logSpy: Spy = spyOn(logService, 'searchLogs').and
        .returnValues(of({ logEntries: [], nextPageToken: 'token' }), of({ logEntries: [] }));
    const timeSpy: Spy = spyOn(timezoneService, 'getResolvedTimestamp').and
        .returnValue(of({ timestamp: 0, message: '' }));

    component.isLoading = false;
    component.isSearching = false;
    component.formModel = {
      logsDateFrom: { year: 2021, month: 6, day: 1 },
      logsTimeFrom: { hour: 23, minute: 59 },
      logsDateTo: { year: 2021, month: 6, day: 2 },
      logsTimeTo: { hour: 23, minute: 59 },
    };
    fixture.detectChanges();

    fixture.debugElement.nativeElement.querySelector('#query-button').click();

    expect(timeSpy).toHaveBeenCalledTimes(2);
    expect(logSpy).toHaveBeenCalledTimes(2);
    expect(logSpy.calls.mostRecent().args).toEqual([{
      searchFrom: '0',
      searchUntil: '0',
      severity: 'ERROR',
      nextPageToken: 'token',
    }]);
  });

  it('should sort logs based on source location', () => {
    const testLog1: GeneralLogEntry = {
      logName: 'stderr',
      severity: 'ERROR',
      trace: 'testTrace1',
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
    spyOn(timezoneService, 'getResolvedTimestamp').and.returnValue(of({ timestamp: 0, message: '' }));

    component.searchForLogs();

    expect(component.searchResult.length).toEqual(2);
  });
});
