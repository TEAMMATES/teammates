import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { LogsPageComponent } from './logs-page.component';
import { LogsPageModule } from './logs-page.module';

describe('LogsPageComponent', () => {
  let component: LogsPageComponent;
  let fixture: ComponentFixture<LogsPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [NgbModule, LogsPageModule, HttpClientTestingModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LogsPageComponent);
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
});
