import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { FeedbackQuestionsService } from './feedback-questions.service';
import { HttpRequestService } from './http-request.service';
import { createMockHttpRequestService, type MockHttpRequestService } from '../test-helpers/mock-http-request';
import { ResourceEndpoints } from '../types/api-const';

describe('FeedbackQuestionsService', () => {
  let spyHttpRequestService: MockHttpRequestService;
  let service: FeedbackQuestionsService;

  beforeEach(() => {
    spyHttpRequestService = createMockHttpRequestService();
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(FeedbackQuestionsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute GET when getting all feedback questions', () => {
    const paramMap: Record<string, string> = {
      fsid: 'fc829c15-3b56-43c1-b932-cc0513cf04d9',
    };

    service.getFeedbackQuestions({
      feedbackSessionId: 'fc829c15-3b56-43c1-b932-cc0513cf04d9',
    });

    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.QUESTIONS, paramMap);
  });
});
