import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LogsHistogramPageComponent } from './logs-histogram-page.component';

describe('LogsHistogramPageComponent', () => {
  let component: LogsHistogramPageComponent;
  let fixture: ComponentFixture<LogsHistogramPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LogsHistogramPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LogsHistogramPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
