import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { FeedbackResponseCommentService } from './feedback-response-comment.service';

describe('FeedbackResponseCommentService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [
      HttpClientTestingModule,
    ],
  }));

  it('should be created', () => {
    const service: FeedbackResponseCommentService = TestBed.inject(FeedbackResponseCommentService);
    expect(service).toBeTruthy();
  });
});
