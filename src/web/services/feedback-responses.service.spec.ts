import { TestBed } from '@angular/core/testing';

import { FeedbackResponsesService } from './feedback-responses.service';

describe('FeedbackResponsesService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: FeedbackResponsesService = TestBed.get(FeedbackResponsesService);
    expect(service).toBeTruthy();
  });
});
