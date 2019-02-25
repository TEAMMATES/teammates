import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FeedbackQuestionsService } from './feedback-questions.service';

describe('FeedbackQuestionsService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [
      HttpClientTestingModule,
    ],
  }));

  it('should be created', () => {
    const service: FeedbackQuestionsService = TestBed.get(FeedbackQuestionsService);
    expect(service).toBeTruthy();
  });
});
