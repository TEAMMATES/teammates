import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { FeedbackQuestionsService } from './feedback-questions.service';
import { HttpRequestService } from './http-request.service';
import createSpyFromClass from '../test-helpers/create-spy-from-class';
import { ResourceEndpoints } from '../types/api-const';
import { Intent } from '../types/api-request';

describe('FeedbackQuestionsService', () => {
  let spyHttpRequestService: any;
  let service: FeedbackQuestionsService;

  beforeEach(() => {
    spyHttpRequestService = createSpyFromClass(HttpRequestService);
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
      intent: Intent.FULL_DETAIL,
      fsid: 'fc829c15-3b56-43c1-b932-cc0513cf04d9',
    };

    service.getFeedbackQuestions({
      feedbackSessionId: 'fc829c15-3b56-43c1-b932-cc0513cf04d9',
      intent: Intent.FULL_DETAIL,
    });

    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.QUESTIONS, paramMap);
  });

  it('should execute GET when loading feedback question recipients', () => {
    const paramMap: Record<string, string> = {
      questionid: '1',
      intent: Intent.STUDENT_SUBMISSION,
      key: 'key',
      moderatedperson: 'John Doe',
      previewas: 'John Doe',
    };

    service.loadFeedbackQuestionRecipients({
      questionId: '1',
      intent: Intent.STUDENT_SUBMISSION,
      key: 'key',
      moderatedPerson: 'John Doe',
      previewAs: 'John Doe',
    });

    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.QUESTION_RECIPIENTS, paramMap);
  });
});
