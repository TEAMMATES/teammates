import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { FeedbackResponseCommentService } from './feedback-response-comment.service';
import { HttpRequestService } from './http-request.service';
import createSpyFromClass from '../test-helpers/create-spy-from-class';
import { ResourceEndpoints } from '../types/api-const';
import {
  CommentVisibilityType,
  FeedbackResponseCommentCreateRequest, FeedbackResponseCommentUpdateRequest,
  Intent,
} from '../types/api-request';

describe('FeedbackResponseCommentService', () => {
  let spyHttpRequestService: any;
  let service: FeedbackResponseCommentService;

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
    service = TestBed.inject(FeedbackResponseCommentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call post when create comment', () => {
    const createRequest: FeedbackResponseCommentCreateRequest = {
      commentText: 'example comment to a response',
      showCommentTo: [CommentVisibilityType.GIVER, CommentVisibilityType.GIVER_TEAM_MEMBERS],
      showGiverNameTo: [CommentVisibilityType.GIVER, CommentVisibilityType.GIVER_TEAM_MEMBERS],
    };
    const responseid: string = 'resp-id-1';
    const intent: Intent = Intent.INSTRUCTOR_RESULT;

    service.createComment(createRequest, responseid, intent);
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(
      ResourceEndpoints.RESPONSE_COMMENT, {
      intent,
      responseid,
    }, createRequest);
  });

  it('should call put when update comment', () => {
    const updateRequest: FeedbackResponseCommentUpdateRequest = {
      commentText: 'updated comment',
      showCommentTo: [CommentVisibilityType.RECIPIENT, CommentVisibilityType.INSTRUCTORS],
      showGiverNameTo: [CommentVisibilityType.RECIPIENT, CommentVisibilityType.INSTRUCTORS],
    };
    const commentId: number = 3;
    const intent: Intent = Intent.INSTRUCTOR_RESULT;

    service.updateComment(updateRequest, commentId, intent);
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(
      ResourceEndpoints.RESPONSE_COMMENT, {
      intent,
      responsecommentid: commentId.toString(),
    },
      updateRequest);
  });

  it('should call delete when delete comment', () => {
    const commentId: number = 2;
    const intent: Intent = Intent.STUDENT_RESULT;

    service.deleteComment(commentId, intent);
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(
      ResourceEndpoints.RESPONSE_COMMENT, {
      intent,
      responsecommentid: commentId.toString(),
    });
  });
});
