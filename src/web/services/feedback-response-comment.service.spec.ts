import { TestBed } from '@angular/core/testing';

import { FeedbackResponseCommentService } from './feedback-response-comment.service';

describe('FeedbackResponseCommentService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: FeedbackResponseCommentService = TestBed.get(FeedbackResponseCommentService);
    expect(service).toBeTruthy();
  });
});
