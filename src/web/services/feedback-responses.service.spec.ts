import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FeedbackResponsesService } from './feedback-responses.service';

describe('FeedbackResponsesService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [
      HttpClientTestingModule,
    ],
  }));

  it('should be created', () => {
    const service: FeedbackResponsesService = TestBed.inject(FeedbackResponsesService);
    expect(service).toBeTruthy();
  });
});
