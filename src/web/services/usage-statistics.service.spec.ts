import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { UsageStatisticsService } from './usage-statistics.service';

describe('UsageStatisticsService', () => {
  let service: UsageStatisticsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
    });
    service = TestBed.inject(UsageStatisticsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

});
