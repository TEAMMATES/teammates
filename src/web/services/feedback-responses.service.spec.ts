import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FeedbackResponsesService } from './feedback-responses.service';

describe('FeedbackResponsesService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [
      HttpClientTestingModule,
      RouterTestingModule,
    ],
  }));

  it('should be created', () => {
    const service: FeedbackResponsesService = TestBed.get(FeedbackResponsesService);
    expect(service).toBeTruthy();
  });
});
