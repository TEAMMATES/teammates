import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { StatsLineChartComponent } from './stats-line-chart.component';

describe('StatsLineChartComponent', () => {
  let component: StatsLineChartComponent;
  let fixture: ComponentFixture<StatsLineChartComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [StatsLineChartComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StatsLineChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
