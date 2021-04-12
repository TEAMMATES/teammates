import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ResourceEndpoints } from '../types/api-const';
import { Intent } from '../types/api-request';
import { FeedbackQuestionsService } from './feedback-questions.service';
import { HttpRequestService } from './http-request.service';

describe('FeedbackQuestionsService', () => {
  let spyHttpRequestService: any;
  let service: FeedbackQuestionsService;

  beforeEach(() => {
    spyHttpRequestService = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      delete: jest.fn(),
    };
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
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
      courseid: 'CS3281',
      fsname: 'feedback session',
    };

    service.getFeedbackQuestions({
      courseId: paramMap.courseid,
      feedbackSessionName: paramMap.fsname,
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
