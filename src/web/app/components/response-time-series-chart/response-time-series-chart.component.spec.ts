import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ResponseTimeSeriesChartComponent } from './response-time-series-chart.component';

describe('ResponseTimeSeriesChartComponent', () => {
  let component: ResponseTimeSeriesChartComponent;
  let fixture: ComponentFixture<ResponseTimeSeriesChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ResponseTimeSeriesChartComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ResponseTimeSeriesChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
