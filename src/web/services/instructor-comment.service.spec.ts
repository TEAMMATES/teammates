import { TestBed } from '@angular/core/testing';
import { InstructorCommentService } from './instructor-comment.service';
import { FeedbackResponseCommentService } from './feedback-response-comment.service';
import { StatusMessageService } from './status-message.service';
import { TableComparatorService } from './table-comparator.service';
import { CommentToCommentRowModelPipe } from '../app/components/comment-box/comment-to-comment-row-model.pipe';
import { HttpRequestService } from './http-request.service';

describe('InstructorCommentService', () => {
  let service: InstructorCommentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        InstructorCommentService,
        { provide: HttpRequestService, useValue: {} },
        { provide: FeedbackResponseCommentService, useValue: {} },
        { provide: StatusMessageService, useValue: {} },
        { provide: TableComparatorService, useValue: {} },
        { provide: CommentToCommentRowModelPipe, useValue: {} },
      ],
    });
    service = TestBed.inject(InstructorCommentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});