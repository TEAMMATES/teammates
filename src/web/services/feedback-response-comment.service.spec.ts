import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ResponseInstructorCommentService } from './feedback-response-comment.service';
import { HttpRequestService } from './http-request.service';
import { createMockHttpRequestService, type MockHttpRequestService } from '../test-helpers/mock-http-request';
import { ResourceEndpoints } from '../types/api-const';
import {
  ResponseInstructorCommentCreateRequest,
  ResponseInstructorCommentUpdateRequest,
} from '../types/api-request';

describe('ResponseInstructorCommentService', () => {
  let spyHttpRequestService: MockHttpRequestService;
  let service: ResponseInstructorCommentService;

  beforeEach(() => {
    spyHttpRequestService = createMockHttpRequestService();
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(ResponseInstructorCommentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call post when create comment', () => {
    const createRequest: ResponseInstructorCommentCreateRequest = {
      commentText: 'example comment to a response',
    };
    const responseid = 'resp-id-1';

    service.createComment(createRequest, responseid);
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(
      ResourceEndpoints.RESPONSE_COMMENT,
      {
        responseid,
      },
      createRequest,
    );
  });

  it('should call put when update comment', () => {
    const updateRequest: ResponseInstructorCommentUpdateRequest = {
      commentText: 'updated comment',
    };
    const commentId = '00000000-0000-4000-8000-000000000003';

    service.updateComment(updateRequest, commentId);
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(
      ResourceEndpoints.RESPONSE_COMMENT,
      {
        responsecommentid: commentId,
      },
      updateRequest,
    );
  });

  it('should call delete when delete comment', () => {
    const commentId = '00000000-0000-4000-8000-000000000002';

    service.deleteComment(commentId);
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.RESPONSE_COMMENT, {
      responsecommentid: commentId,
    });
  });
});
