import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FeedbackSessionsService } from './feedback-sessions.service';

describe('FeedbackSessionsService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [
      HttpClientTestingModule,
      RouterTestingModule,
    ],
  }));

  it('should be created', () => {
    const service: FeedbackSessionsService = TestBed.get(FeedbackSessionsService);
    expect(service).toBeTruthy();
  });
});
