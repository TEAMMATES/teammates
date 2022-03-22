import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbDatepickerModule, NgbTimepickerModule } from '@ng-bootstrap/ng-bootstrap';

import { StatsLineChartComponent } from './stats-line-chart/stats-line-chart.component';
import { UsageStatisticsPageComponent } from './usage-statistics-page.component';

describe('UsageStatisticsPageComponent', () => {
  let component: UsageStatisticsPageComponent;
  let fixture: ComponentFixture<UsageStatisticsPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        UsageStatisticsPageComponent,
        StatsLineChartComponent,
      ],
      imports: [
        NgbDatepickerModule,
        NgbTimepickerModule,
        FormsModule,
        HttpClientTestingModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UsageStatisticsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
