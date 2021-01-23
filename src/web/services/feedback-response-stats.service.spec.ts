import { TestBed } from '@angular/core/testing';

import { FeedbackResponseStatsService } from './feedback-response-stats.service';

describe('FeedbackResponseStatsService', () => {
  let service: FeedbackResponseStatsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FeedbackResponseStatsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
