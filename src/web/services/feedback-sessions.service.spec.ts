import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SessionsTableRowModel } from '../app/components/sessions-table/sessions-table-model';
import { ResourceEndpoints } from '../types/api-endpoints';
import {
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../types/api-output';
import { DEFAULT_INSTRUCTOR_PRIVILEGE } from '../types/instructor-privilege';
import { FeedbackSessionsService } from './feedback-sessions.service';
import { HttpRequestService } from './http-request.service';

describe('FeedbackSessionsService', () => {
  let spyHttpRequestService: any;
  let service: FeedbackSessionsService;
  let model: SessionsTableRowModel;

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
    service = TestBed.get(FeedbackSessionsService);
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
        isClosingEmailEnabled: false,
        isPublishedEmailEnabled: false,
        createdAtTimestamp: 0,
      },
      responseRate: '',
      isLoadingResponseRate: false,
      instructorPrivilege: DEFAULT_INSTRUCTOR_PRIVILEGE,
    };
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
    const paramMap: { [key: string]: string } = {
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
    const paramMap: { [key: string]: string } = {
      courseid: model.feedbackSession.courseId,
      fsname: model.feedbackSession.feedbackSessionName,
    };
    service.loadSessionStatistics(
        model.feedbackSession.courseId,
        model.feedbackSession.feedbackSessionName,
    );
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.SESSION_STATS, paramMap);
  });

  it('should call put when moving session to recycle bin', () => {
    const paramMap: { [key: string]: string } = {
      courseid: 'CS3281',
      fsname: 'test feedback session',
    };

    service.moveSessionToRecycleBin(paramMap.courseid, paramMap.fsname);
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.BIN_SESSION, paramMap);
  });

  it('should call delete when deleting session from recycle bin', () => {
    const paramMap: { [key: string]: string } = {
      courseid: 'CS3281',
      fsname: 'test feedback session',
    };

    service.deleteFeedbackSession(paramMap.courseid, paramMap.fsname);
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.SESSION, paramMap);
  });
});
