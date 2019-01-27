import { TestBed } from '@angular/core/testing';

import { FeedbackQuestionsService } from './feedback-questions.service';

describe('FeedbackQuestionsService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: FeedbackQuestionsService = TestBed.get(FeedbackQuestionsService);
    expect(service).toBeTruthy();
  });
});
