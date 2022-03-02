import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { LogsHistogramComponent } from './logs-histogram.component';

describe('LogsHistogramComponent', () => {
  let component: LogsHistogramComponent;
  let fixture: ComponentFixture<LogsHistogramComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [LogsHistogramComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LogsHistogramComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
