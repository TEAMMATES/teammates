import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FeedbackResponseCommentService } from './feedback-response-comment.service';

describe('FeedbackResponseCommentService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [
      HttpClientTestingModule,
    ],
  }));

  it('should be created', () => {
    const service: FeedbackResponseCommentService = TestBed.get(FeedbackResponseCommentService);
    expect(service).toBeTruthy();
  });
});
