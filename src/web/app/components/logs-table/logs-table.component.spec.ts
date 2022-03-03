import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { LogDetailsModule } from './log-details/log-details.module';
import { LogLineModule } from './log-line/log-line.module';
import { LogsTableComponent } from './logs-table.component';

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
});
