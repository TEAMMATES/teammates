import { HttpClientTestingModule } from '@angular/common/http/testing';
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
    studentDeadlines: {},
    instructorDeadlines: {},
  };

  beforeEach(() => {
    spyHttpRequestService = createSpyFromClass(HttpRequestService);
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
      ],
    });
    service = TestBed.inject(FeedbackSessionsService);
    model = {
      feedbackSession: {
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
        studentDeadlines: {},
        instructorDeadlines: {},
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
      courseid: model.feedbackSession.courseId,
      fsname: model.feedbackSession.feedbackSessionName,
    };

    service.publishFeedbackSession(
        model.feedbackSession.courseId,
        model.feedbackSession.feedbackSessionName,
    );
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.SESSION_PUBLISH, paramMap);
  });

  it('should call delete when unpublishing', () => {
    const paramMap: Record<string, string> = {
      courseid: model.feedbackSession.courseId,
      fsname: model.feedbackSession.feedbackSessionName,
    };

    service.unpublishFeedbackSession(
        model.feedbackSession.courseId,
        model.feedbackSession.feedbackSessionName,
    );
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.SESSION_PUBLISH, paramMap);
  });

  it('should call get when loading session statistics', () => {
    const paramMap: Record<string, string> = {
      courseid: model.feedbackSession.courseId,
      fsname: model.feedbackSession.feedbackSessionName,
    };
    service.loadSessionStatistics(
        model.feedbackSession.courseId,
        model.feedbackSession.feedbackSessionName,
    );
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.SESSION_STATS, paramMap);
  });

  it('should call get when retrieving feedback session results', () => {
    const paramMap: Record<string, string> = {
      courseid: 'CS3281',
      fsname: 'test feedback session',
      intent: Intent.FULL_DETAIL,
    };

    service.getFeedbackSessionResults({
      courseId: paramMap['courseid'],
      feedbackSessionName: paramMap['fsname'],
      intent: Intent.FULL_DETAIL,
    });
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.RESULT, paramMap);
  });

  it('should call put when moving session to recycle bin', () => {
    const paramMap: Record<string, string> = {
      courseid: 'CS3281',
      fsname: 'test feedback session',
    };

    service.moveSessionToRecycleBin(paramMap['courseid'], paramMap['fsname']);
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.BIN_SESSION, paramMap);
  });

  it('should call delete when removing session from recycle bin', () => {
    const paramMap: Record<string, string> = {
      courseid: 'CS3281',
      fsname: 'test feedback session',
    };

    service.deleteSessionFromRecycleBin(paramMap['courseid'], paramMap['fsname']);
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.BIN_SESSION, paramMap);
  });

  it('should call delete when deleting session from recycle bin', () => {
    const paramMap: Record<string, string> = {
      courseid: 'CS3281',
      fsname: 'test feedback session',
    };

    service.deleteFeedbackSession(paramMap['courseid'], paramMap['fsname']);
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.SESSION, paramMap);
  });

  it('should return true if a feedbackSession is no longer open', () => {
    expect(service.isFeedbackSessionOpen(mockFeedbackSession)).toBeTruthy();
  });

  it('should return true if the feedback session has been published', () => {
    expect(service.isFeedbackSessionPublished(mockFeedbackSession)).toBeTruthy();
  });
});
