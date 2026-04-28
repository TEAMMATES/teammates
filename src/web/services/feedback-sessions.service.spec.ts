import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { FeedbackSessionsService } from './feedback-sessions.service';
import { HttpRequestService } from './http-request.service';
import { SessionsTableRowModel } from '../app/components/sessions-table/sessions-table-model';
import createSpyFromClass from '../test-helpers/create-spy-from-class';
import { ResourceEndpoints } from '../types/api-const';
import {
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../types/api-output';
import { Intent } from '../types/api-request';
import { DEFAULT_INSTRUCTOR_PRIVILEGE } from '../types/default-instructor-privilege';

describe('FeedbackSessionsService', () => {
  let spyHttpRequestService: any;
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
    sessionVisibleSetting: SessionVisibleSetting.CUSTOM,
    responseVisibleSetting: ResponseVisibleSetting.CUSTOM,
    isClosingSoonEmailEnabled: false,
    isPublishedEmailEnabled: false,
  };

  beforeEach(() => {
    spyHttpRequestService = createSpyFromClass(HttpRequestService);
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
        sessionVisibleSetting: SessionVisibleSetting.CUSTOM,
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
      fsid: model.feedbackSession.feedbackSessionId,
    };

    service.publishFeedbackSession(
        model.feedbackSession.feedbackSessionId,
    );
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.SESSION_PUBLISH, paramMap);
  });

  it('should call delete when unpublishing', () => {
    const paramMap: Record<string, string> = {
      fsid: model.feedbackSession.feedbackSessionId,
    };

    service.unpublishFeedbackSession(
        model.feedbackSession.feedbackSessionId,
    );
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.SESSION_PUBLISH, paramMap);
  });

  it('should call get when loading session statistics', () => {
    const paramMap: Record<string, string> = {
      fsid: model.feedbackSession.feedbackSessionId,
    };
    service.loadSessionStatistics(
        model.feedbackSession.feedbackSessionId,
    );
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.SESSION_STATS, paramMap);
  });

  it('should call get when retrieving feedback session results', () => {
    const paramMap: Record<string, string> = {
      fsid: '248b1915-5f52-4730-b5b2-3ec25a2caabc',
      intent: Intent.FULL_DETAIL,
    };

    service.getFeedbackSessionResults({
      feedbackSessionId: paramMap['fsid'],
      intent: Intent.FULL_DETAIL,
    });
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.RESULT, paramMap);
  });

  it('should call put when moving session to recycle bin', () => {
    const paramMap: Record<string, string> = {
      fsid: '213bccdb-1c83-45b6-8643-2c9ab7b03837',
    };

    service.moveSessionToRecycleBin(paramMap['fsid']);
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.BIN_SESSION, paramMap);
  });

  it('should call delete when restoring session from recycle bin', () => {
    const paramMap: Record<string, string> = {
      fsid: '213bccdb-1c83-45b6-8643-2c9ab7b03837',
    };

    service.restoreSessionFromRecycleBin(paramMap['fsid']);
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.BIN_SESSION, paramMap);
  });

  it('should call delete when deleting feedback session', () => {
    const paramMap: Record<string, string> = {
      fsid: 'bae3cb90-13dd-45f5-882e-250a43b1ee6f',
    };

    service.deleteFeedbackSession(paramMap['fsid']);
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.SESSION, paramMap);
  });

  it('should return true if a feedbackSession is no longer open', () => {
    expect(service.isFeedbackSessionOpen(mockFeedbackSession)).toBeTruthy();
  });

  it('should return true if the feedback session has been published', () => {
    expect(service.isFeedbackSessionPublished(mockFeedbackSession)).toBeTruthy();
  });

  it('should execute GET to check responses for all feedback sessions in a course', () => {
    const courseId: string = 'test-id';
    const paramMap: { [key: string]: string } = {
      entitytype: 'instructor',
      courseid: courseId,
    };
    service.hasResponsesForAllFeedbackSessionsInCourse(courseId, 'instructor');
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.HAS_RESPONSES, paramMap);
  });
});
