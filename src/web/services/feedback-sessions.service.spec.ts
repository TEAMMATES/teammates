import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FeedbackSessionsService } from './feedback-sessions.service';

describe('FeedbackSessionsService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [
      HttpClientTestingModule,
    ],
  }));

  it('should be created', () => {
    const service: FeedbackSessionsService = TestBed.get(FeedbackSessionsService);
    expect(service).toBeTruthy();
  });
});
