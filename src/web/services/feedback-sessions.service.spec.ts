import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { FeedbackSessionsService } from './feedback-sessions.service';
import { HttpRequestService } from './http-request.service';
import { createMockHttpRequestService, type MockHttpRequestService } from '../test-helpers/mock-http-request';
import { SessionsTableRowModel } from '../app/components/sessions-table/sessions-table-model';
import { QueryParamKeys, ResourceEndpoints } from '../types/api-const';
import {
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
} from '../types/api-output';
import { Intent, SessionKeyType } from '../types/api-request';
import { DEFAULT_INSTRUCTOR_PRIVILEGE } from '../types/default-instructor-privilege';

describe('FeedbackSessionsService', () => {
  let spyHttpRequestService: MockHttpRequestService;
  let service: FeedbackSessionsService;
  let model: SessionsTableRowModel;

  const mockFeedbackSession: FeedbackSession = {
    feedbackSessionId: 'c64aa0ca-beba-412d-94c3-58134feb6822',
    courseId: 'dog.gma-demo',
    timeZone: 'Asia/Singapore',
    feedbackSessionName: 'First team feedback session',
    instructions: 'Please give your feedback based on the following questions.',
    submissionStartTimestamp: 1333295940000,
    submissionEndTimestamp: 1333382340000,
    submissionStatus: FeedbackSessionSubmissionStatus.CLOSED,
    publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
    createdAtTimestamp: 1333324740000,
    gracePeriod: 1,
    responseVisibleSetting: ResponseVisibleSetting.CUSTOM,
    isClosingSoonEmailEnabled: false,
    isPublishedEmailEnabled: false,
  };

  beforeEach(() => {
    spyHttpRequestService = createMockHttpRequestService();
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(FeedbackSessionsService);
    model = {
      feedbackSession: {
        feedbackSessionId: '248b1915-5f52-4730-b5b2-3ec25a2caabc',
        courseId: 'CS3281',
        timeZone: '',
        feedbackSessionName: '',
        instructions: '',
        submissionStartTimestamp: 0,
        submissionEndTimestamp: 0,
        gracePeriod: 0,
        responseVisibleSetting: ResponseVisibleSetting.CUSTOM,
        submissionStatus: FeedbackSessionSubmissionStatus.CLOSED,
        publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
        isClosingSoonEmailEnabled: false,
        isPublishedEmailEnabled: false,
        createdAtTimestamp: 0,
      },
      responseRate: '',
      isLoadingResponseRate: false,
      instructorPrivilege: DEFAULT_INSTRUCTOR_PRIVILEGE(),
    };
    mockFeedbackSession.submissionStartTimestamp = Date.now() - 100000;
    mockFeedbackSession.submissionEndTimestamp = Date.now() + 100000;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call post when publishing', () => {
    const paramMap: { [key: string]: string } = {
      [QueryParamKeys.FEEDBACK_SESSION_ID]: model.feedbackSession.feedbackSessionId,
    };

    service.publishFeedbackSession(model.feedbackSession.feedbackSessionId);
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.SESSION_PUBLISH, paramMap);
  });

  it('should call delete when unpublishing', () => {
    const paramMap: Record<string, string> = {
      [QueryParamKeys.FEEDBACK_SESSION_ID]: model.feedbackSession.feedbackSessionId,
    };

    service.unpublishFeedbackSession(model.feedbackSession.feedbackSessionId);
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.SESSION_PUBLISH, paramMap);
  });

  it('should call get when loading session statistics', () => {
    const paramMap: Record<string, string> = {
      [QueryParamKeys.FEEDBACK_SESSION_ID]: model.feedbackSession.feedbackSessionId,
    };
    service.loadSessionStatistics(model.feedbackSession.feedbackSessionId);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.SESSION_STATS, paramMap);
  });

  it('should call post when preflighting session key access', () => {
    const request = {
      feedbackSessionId: 'session-id',
      key: 'session-key',
      type: SessionKeyType.SUBMISSION,
    };

    service.checkSessionKeyAccess(request);

    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.SESSION_KEY_ACCESS, {}, request);
  });

  it('should call get when retrieving course feedback session results', () => {
    const paramMap: Record<string, string> = {
      [QueryParamKeys.FEEDBACK_SESSION_ID]: '248b1915-5f52-4730-b5b2-3ec25a2caabc',
    };

    service.getCourseSessionResults({
      feedbackSessionId: paramMap[QueryParamKeys.FEEDBACK_SESSION_ID],
    });
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.COURSE_SESSION_RESULTS, paramMap);
  });

  it('should call get when retrieving user feedback session results', () => {
    const paramMap: Record<string, string> = {
      [QueryParamKeys.FEEDBACK_SESSION_ID]: '248b1915-5f52-4730-b5b2-3ec25a2caabc',
      [QueryParamKeys.USER_ID]: 'student-user-id',
      [QueryParamKeys.IS_PREVIEW]: 'false',
    };

    service.getUserSessionResults({
      feedbackSessionId: paramMap[QueryParamKeys.FEEDBACK_SESSION_ID],
      userId: paramMap[QueryParamKeys.USER_ID],
      isPreview: false,
    });
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.USER_SESSION_RESULTS, paramMap);
  });

  it('should call get when retrieving feedback session submission data', () => {
    const paramMap: Record<string, string> = {
      [QueryParamKeys.FEEDBACK_SESSION_ID]: '248b1915-5f52-4730-b5b2-3ec25a2caabc',
      intent: Intent.STUDENT_SUBMISSION,
      [QueryParamKeys.KEY]: 'reg-key',
      [QueryParamKeys.FEEDBACK_SESSION_MODERATED_PERSON]: 'moderated-person',
      [QueryParamKeys.PREVIEWAS]: 'preview-person',
    };

    service.getSessionSubmissionData({
      feedbackSessionId: paramMap[QueryParamKeys.FEEDBACK_SESSION_ID],
      intent: Intent.STUDENT_SUBMISSION,
      key: paramMap[QueryParamKeys.KEY],
      moderatedPerson: paramMap[QueryParamKeys.FEEDBACK_SESSION_MODERATED_PERSON],
      previewAs: paramMap[QueryParamKeys.PREVIEWAS],
    });
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.SESSION_SUBMISSION, paramMap);
  });

  it('should call put when moving session to recycle bin', () => {
    const paramMap: Record<string, string> = {
      [QueryParamKeys.FEEDBACK_SESSION_ID]: '213bccdb-1c83-45b6-8643-2c9ab7b03837',
    };

    service.moveSessionToRecycleBin(paramMap[QueryParamKeys.FEEDBACK_SESSION_ID]);
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.BIN_SESSION, paramMap);
  });

  it('should call delete when restoring session from recycle bin', () => {
    const paramMap: Record<string, string> = {
      [QueryParamKeys.FEEDBACK_SESSION_ID]: '213bccdb-1c83-45b6-8643-2c9ab7b03837',
    };

    service.restoreSessionFromRecycleBin(paramMap[QueryParamKeys.FEEDBACK_SESSION_ID]);
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.BIN_SESSION, paramMap);
  });

  it('should call delete when deleting feedback session', () => {
    const paramMap: Record<string, string> = {
      [QueryParamKeys.FEEDBACK_SESSION_ID]: 'bae3cb90-13dd-45f5-882e-250a43b1ee6f',
    };

    service.deleteFeedbackSession(paramMap[QueryParamKeys.FEEDBACK_SESSION_ID]);
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.SESSION, paramMap);
  });

  it('should call get when retrieving feedback sessions for a student', () => {
    const courseId = 'CS1231';
    const paramMap: Record<string, string> = {
      courseid: courseId,
    };

    service.getFeedbackSessionsForStudent(courseId);

    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.STUDENT_SESSIONS, paramMap);
  });

  it('should return true if a feedbackSession is no longer open', () => {
    expect(service.isFeedbackSessionOpen(mockFeedbackSession)).toBeTruthy();
  });

  it('should return true if the feedback session has been published', () => {
    expect(service.isFeedbackSessionPublished(mockFeedbackSession)).toBeTruthy();
  });

  it('should execute GET to check responses for all feedback sessions in a course', () => {
    const courseId = 'test-id';
    const paramMap: { [key: string]: string } = {
      entitytype: 'instructor',
      [QueryParamKeys.COURSE_ID]: courseId,
    };
    service.hasResponsesForAllFeedbackSessionsInCourse(courseId, 'instructor');
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.HAS_RESPONSES, paramMap);
  });

  it('should execute GET to get deadline extension for a user in a feedback session', () => {
    const feedbackSessionId = 'test-session-id';
    const userId = 'test-user-id';
    const paramMap: { [key: string]: string } = {
      [QueryParamKeys.FEEDBACK_SESSION_ID]: feedbackSessionId,
      [QueryParamKeys.USER_ID]: userId,
    };
    service.getDeadlineExtension({ feedbackSessionId, userId });
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.SESSION_DEADLINE_EXTENSION, paramMap);
  });
});
