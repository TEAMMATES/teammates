import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbDatepickerModule, NgbTimepickerModule } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { StatsLineChartComponent } from './stats-line-chart/stats-line-chart.component';
import { AggregationType, StatisticsType, UsageStatisticsPageComponent } from './usage-statistics-page.component';
import { UsageStatisticsService } from '../../../services/usage-statistics.service';
import { UsageStatistics } from '../../../types/api-output';
import { Milliseconds } from '../../../types/datetime-const';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';

const generateData = (startTime: number, iterations: number): UsageStatistics[] => {
  const stats = [];
  let time = startTime;
  for (let i = 1; i <= iterations; i += 1) {
    stats.push({
      startTime: time,
      timePeriod: 60,
      numResponses: i,
      numCourses: 0,
      numStudents: 0,
      numInstructors: 0,
      numAccountRequests: 0,
      numEmails: 0,
      numSubmissions: 0,
    });
    time += Milliseconds.IN_ONE_HOUR;
  }
  return stats;
};

describe('UsageStatisticsPageComponent', () => {
  let component: UsageStatisticsPageComponent;
  let fixture: ComponentFixture<UsageStatisticsPageComponent>;
  let usageStatisticsService: UsageStatisticsService;

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
        LoadingSpinnerModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UsageStatisticsPageComponent);
    component = fixture.componentInstance;
    usageStatisticsService = TestBed.inject(UsageStatisticsService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should fetch usage statistics successfully', () => {
    component.timezone = 'UTC';
    component.formModel = {
      fromDate: { year: 2022, month: 3, day: 27 },
      fromTime: { hour: 22, minute: 45 },
      toDate: { year: 2022, month: 3, day: 28 },
      toTime: { hour: 0, minute: 15 },
      dataType: StatisticsType.NUM_RESPONSES,
      aggregationType: AggregationType.HOURLY,
    };

    const statsObjects: UsageStatistics[] = [{
      startTime: new Date('2022-03-27T23:00:00Z').getTime(),
      timePeriod: 60,
      numResponses: 100,
      numCourses: 3,
      numStudents: 2,
      numInstructors: 2,
      numAccountRequests: 1,
      numEmails: 50,
      numSubmissions: 99,
    }, {
      startTime: new Date('2022-03-28T00:00:00Z').getTime(),
      timePeriod: 60,
      numResponses: 400,
      numCourses: 1,
      numStudents: 1,
      numInstructors: 5,
      numAccountRequests: 2,
      numEmails: 61,
      numSubmissions: 71,
    }];

    const spy = jest.spyOn(usageStatisticsService, 'getUsageStatistics').mockReturnValue(of({
      result: statsObjects,
    }));

    fixture.detectChanges();

    component.getUsageStatistics();

    expect(spy).toHaveBeenCalledTimes(1);
    expect(component.hasQueried).toBe(true);
    expect(component.isLoading).toBe(false);
    expect(component.fetchedData).toEqual(statsObjects);
    expect(component.timeRange).toEqual({
      startTime: new Date('2022-03-27T22:45:00Z').getTime(),
      endTime: new Date('2022-03-28T00:15:00Z').getTime(),
    });
  });

  it('should process fetched data correctly', () => {
    component.formModel.dataType = StatisticsType.NUM_RESPONSES;
    component.formModel.aggregationType = AggregationType.HOURLY;
    const startTime = new Date('2022-01-27T22:00:00Z').getTime();
    const generatedData = generateData(startTime, 3 * 24);
    component.fetchedData = generatedData;

    fixture.detectChanges();

    component.drawLineChart();

    expect(component.itemName).toEqual('responses');
    expect(component.dataToDraw).toEqual(generatedData.map((us: UsageStatistics) => ({
      value: us.numResponses,
      date: new Date(us.startTime).toISOString(),
    })));
  });

  it('should aggregate data by day correctly', () => {
    component.formModel.dataType = StatisticsType.NUM_RESPONSES;
    component.formModel.aggregationType = AggregationType.DAILY;
    const startTime = new Date('2022-01-27T22:00:00Z').getTime();
    component.fetchedData = generateData(startTime, 10 * 24);

    fixture.detectChanges();

    component.drawLineChart();

    const expectedDataToDraw = [
      {
        date: '2022-01-27T22:00:00.000Z',
        value: 3,
      },
      {
        date: '2022-01-28T00:00:00.000Z',
        value: 348,
      },
      {
        date: '2022-01-29T00:00:00.000Z',
        value: 924,
      },
      {
        date: '2022-01-30T00:00:00.000Z',
        value: 1500,
      },
      {
        date: '2022-01-31T00:00:00.000Z',
        value: 2076,
      },
      {
        date: '2022-02-01T00:00:00.000Z',
        value: 2652,
      },
      {
        date: '2022-02-02T00:00:00.000Z',
        value: 3228,
      },
      {
        date: '2022-02-03T00:00:00.000Z',
        value: 3804,
      },
      {
        date: '2022-02-04T00:00:00.000Z',
        value: 4380,
      },
      {
        date: '2022-02-05T00:00:00.000Z',
        value: 4956,
      },
      {
        date: '2022-02-06T00:00:00.000Z',
        value: 5049,
      },
    ];

    expect(component.itemName).toEqual('responses');
    expect(component.dataToDraw).toEqual(expectedDataToDraw);
  });

  it('should force apply aggregation if there are too many data points', () => {
    component.formModel.dataType = StatisticsType.NUM_STUDENTS;
    component.formModel.aggregationType = AggregationType.HOURLY;
    const startTime = new Date('2022-01-27T22:00:00Z').getTime();
    component.fetchedData = generateData(startTime, 31 * 24);

    fixture.detectChanges();

    component.drawLineChart();

    expect(component.itemName).toEqual('students');
    expect(component.dataToDraw.length).toEqual(32);
  });

  it('should not apply aggregation if there are too few data points', () => {
    component.formModel.dataType = StatisticsType.NUM_COURSES;
    component.formModel.aggregationType = AggregationType.DAILY;
    const startTime = new Date('2022-01-27T22:00:00Z').getTime();
    component.fetchedData = generateData(startTime, 6 * 24);

    fixture.detectChanges();

    component.drawLineChart();

    expect(component.itemName).toEqual('courses');
    expect(component.dataToDraw.length).toEqual(144);
  });

});
