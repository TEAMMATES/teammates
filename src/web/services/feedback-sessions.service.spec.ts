import { TestBed } from '@angular/core/testing';

import { FeedbackSessionsService } from './feedback-sessions.service';

describe('FeedbackSessionsService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: FeedbackSessionsService = TestBed.get(FeedbackSessionsService);
    expect(service).toBeTruthy();
  });
});
