import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { FeedbackResponseCommentService } from './feedback-response-comment.service';
import { InstructorCommentService } from './instructor-comment.service';
import { StatusMessageService } from './status-message.service';
import { TableComparatorService } from './table-comparator.service';
import createSpyFromClass from '../test-helpers/create-spy-from-class';
import { CommentVisibilityType, FeedbackResponseComment } from '../types/api-output';
import { Intent } from '../types/api-request';
import { SortBy, SortOrder } from '../types/sort-properties';
import { CommentRowModel } from '../app/components/comment-box/comment-row/comment-row.component';
import { CommentTableModel } from '../app/components/comment-box/comment-table/comment-table.model';
import { CommentToCommentRowModelPipe } from '../app/components/comment-box/comment-to-comment-row-model.pipe';

describe('InstructorCommentService', () => {
  const timezone = 'Asia/Singapore';
  const errorMessage = 'Something went wrong';

  let spyFeedbackResponseCommentService: any;
  let spyStatusMessageService: any;
  let spyTableComparatorService: any;
  let commentToCommentRowModelPipe: CommentToCommentRowModelPipe;
  let service: InstructorCommentService;

  const createComment = (overrides: Partial<FeedbackResponseComment> = {}): FeedbackResponseComment => ({
    commentGiver: 'instructor@example.com',
    lastEditorEmail: 'instructor@example.com',
    feedbackResponseCommentId: 'comment-id',
    commentText: 'comment text',
    createdAt: 1000,
    lastEditedAt: 1000,
    isVisibilityFollowingFeedbackQuestion: false,
    showGiverNameTo: [CommentVisibilityType.INSTRUCTORS],
    showCommentTo: [CommentVisibilityType.INSTRUCTORS],
    ...overrides,
  });

  const createCommentRow = (comment: FeedbackResponseComment = createComment()): CommentRowModel => ({
    timezone,
    originalComment: comment,
    commentGiverName: 'Original Instructor',
    lastEditorName: 'Original Instructor',
    commentEditFormModel: {
      commentText: comment.commentText,
      isUsingCustomVisibilities: false,
      showCommentTo: comment.showCommentTo,
      showGiverNameTo: comment.showGiverNameTo,
    },
    isEditing: false,
  });

  const createNewCommentRow = (): CommentRowModel => ({
    commentEditFormModel: {
      commentText: 'new comment text',
      isUsingCustomVisibilities: true,
      showCommentTo: [CommentVisibilityType.RECIPIENT],
      showGiverNameTo: [CommentVisibilityType.RECIPIENT],
    },
    isEditing: true,
  });

  const createCommentTableModel = (commentRows: CommentRowModel[] = []): CommentTableModel => ({
    commentRows,
    newCommentRow: createNewCommentRow(),
    isAddingNewComment: true,
    isReadOnly: false,
  });

  beforeEach(() => {
    spyFeedbackResponseCommentService = createSpyFromClass(FeedbackResponseCommentService);
    spyStatusMessageService = createSpyFromClass(StatusMessageService);
    spyTableComparatorService = createSpyFromClass(TableComparatorService);
    commentToCommentRowModelPipe = new CommentToCommentRowModelPipe();
    spyTableComparatorService.compare.mockReturnValue(0);

    TestBed.configureTestingModule({
      providers: [
        { provide: FeedbackResponseCommentService, useValue: spyFeedbackResponseCommentService },
        { provide: StatusMessageService, useValue: spyStatusMessageService },
        { provide: TableComparatorService, useValue: spyTableComparatorService },
        { provide: CommentToCommentRowModelPipe, useValue: commentToCommentRowModelPipe },
      ],
    });
    service = TestBed.inject(InstructorCommentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should delete an instructor comment', () => {
    const commentTableModel: CommentTableModel = createCommentTableModel([
      createCommentRow(createComment({ feedbackResponseCommentId: 'comment-id-1' })),
      createCommentRow(createComment({ feedbackResponseCommentId: 'comment-id-2' })),
    ]);
    const instructorCommentTableModel: Record<string, CommentTableModel> = {
      'response-id': commentTableModel,
    };
    spyFeedbackResponseCommentService.deleteComment.mockReturnValue(of({}));

    service.deleteComment({
      data: { responseId: 'response-id', index: 0 },
      instructorCommentTableModel,
    });

    expect(spyFeedbackResponseCommentService.deleteComment).toHaveBeenCalledWith(
      'comment-id-1',
      Intent.INSTRUCTOR_RESULT,
    );
    expect(instructorCommentTableModel['response-id'].commentRows).toHaveLength(1);
    expect(instructorCommentTableModel['response-id'].commentRows[0].originalComment?.feedbackResponseCommentId).toBe(
      'comment-id-2',
    );
  });

  it('should show error toast when deleting an instructor comment fails', () => {
    const commentTableModel: CommentTableModel = createCommentTableModel([createCommentRow()]);
    const instructorCommentTableModel: Record<string, CommentTableModel> = {
      'response-id': commentTableModel,
    };
    spyFeedbackResponseCommentService.deleteComment.mockReturnValue(
      throwError(() => ({ error: { message: errorMessage } })),
    );

    service.deleteComment({
      data: { responseId: 'response-id', index: 0 },
      instructorCommentTableModel,
    });

    expect(spyStatusMessageService.showErrorToast).toHaveBeenCalledWith(errorMessage);
    expect(instructorCommentTableModel['response-id'].commentRows).toHaveLength(1);
  });

  it('should update an instructor comment', () => {
    const originalComment: FeedbackResponseComment = createComment({
      feedbackResponseCommentId: 'comment-id-to-update',
      commentText: 'old text',
    });
    const commentTableModel: CommentTableModel = createCommentTableModel([createCommentRow(originalComment)]);
    commentTableModel.commentRows[0].commentEditFormModel = {
      commentText: 'updated text',
      isUsingCustomVisibilities: true,
      showCommentTo: [CommentVisibilityType.RECIPIENT],
      showGiverNameTo: [CommentVisibilityType.RECIPIENT],
    };
    const updatedComment: FeedbackResponseComment = createComment({
      feedbackResponseCommentId: 'comment-id-to-update',
      commentText: 'updated text',
      createdAt: 1000,
      lastEditedAt: 2000,
      showCommentTo: [CommentVisibilityType.RECIPIENT],
      showGiverNameTo: [CommentVisibilityType.RECIPIENT],
    });
    const instructorCommentTableModel: Record<string, CommentTableModel> = {
      'response-id': commentTableModel,
    };
    spyFeedbackResponseCommentService.updateComment.mockReturnValue(of(updatedComment));

    service.updateComment({
      data: { responseId: 'response-id', index: 0 },
      timezone,
      instructorCommentTableModel,
      currInstructorName: 'Current Instructor',
    });

    expect(spyFeedbackResponseCommentService.updateComment).toHaveBeenCalledWith(
      {
        commentText: 'updated text',
        showCommentTo: [CommentVisibilityType.RECIPIENT],
        showGiverNameTo: [CommentVisibilityType.RECIPIENT],
      },
      'comment-id-to-update',
      Intent.INSTRUCTOR_RESULT,
    );
    expect(instructorCommentTableModel['response-id'].commentRows[0].originalComment?.commentText).toBe('updated text');
    expect(instructorCommentTableModel['response-id'].commentRows[0].commentGiverName).toBe('Original Instructor');
    expect(instructorCommentTableModel['response-id'].commentRows[0].lastEditorName).toBe('Current Instructor');
    expect(instructorCommentTableModel['response-id'].commentRows[0].timezone).toBe(timezone);
  });

  it('should show error toast when updating an instructor comment fails', () => {
    const commentTableModel: CommentTableModel = createCommentTableModel([createCommentRow()]);
    const instructorCommentTableModel: Record<string, CommentTableModel> = {
      'response-id': commentTableModel,
    };
    spyFeedbackResponseCommentService.updateComment.mockReturnValue(
      throwError(() => ({ error: { message: errorMessage } })),
    );

    service.updateComment({
      data: { responseId: 'response-id', index: 0 },
      timezone,
      instructorCommentTableModel,
    });

    expect(spyStatusMessageService.showErrorToast).toHaveBeenCalledWith(errorMessage);
    expect(instructorCommentTableModel['response-id'].commentRows[0].originalComment?.commentText).toBe('comment text');
  });

  it('should save a new instructor comment and keep comments sorted', () => {
    const oldComment: FeedbackResponseComment = createComment({
      feedbackResponseCommentId: 'old-comment-id',
      commentText: 'old comment',
      createdAt: 2000,
    });
    const newComment: FeedbackResponseComment = createComment({
      feedbackResponseCommentId: 'new-comment-id',
      commentText: 'new comment text',
      createdAt: 1000,
    });
    const commentTableModel: CommentTableModel = createCommentTableModel([createCommentRow(oldComment)]);
    const instructorCommentTableModel: Record<string, CommentTableModel> = {
      'response-id': commentTableModel,
    };
    spyFeedbackResponseCommentService.createComment.mockReturnValue(of(newComment));
    spyTableComparatorService.compare.mockImplementation(
      (_sortBy: SortBy, _sortOrder: SortOrder, strA: string, strB: string) => Number(strA) - Number(strB),
    );

    service.saveNewComment({
      responseId: 'response-id',
      timezone,
      instructorCommentTableModel,
      currInstructorName: 'Current Instructor',
    });

    expect(spyFeedbackResponseCommentService.createComment).toHaveBeenCalledWith(
      {
        commentText: 'new comment text',
        showCommentTo: [CommentVisibilityType.RECIPIENT],
        showGiverNameTo: [CommentVisibilityType.RECIPIENT],
      },
      'response-id',
      Intent.INSTRUCTOR_RESULT,
    );
    expect(instructorCommentTableModel['response-id'].commentRows).toHaveLength(2);
    expect(instructorCommentTableModel['response-id'].commentRows[0].originalComment?.feedbackResponseCommentId).toBe(
      'new-comment-id',
    );
    expect(instructorCommentTableModel['response-id'].commentRows[0].commentGiverName).toBe('Current Instructor');
    expect(instructorCommentTableModel['response-id'].commentRows[0].lastEditorName).toBe('Current Instructor');
    expect(instructorCommentTableModel['response-id'].newCommentRow.commentEditFormModel.commentText).toBe('');
    expect(instructorCommentTableModel['response-id'].isAddingNewComment).toBe(false);
  });

  it('should show error toast when saving a new instructor comment fails', () => {
    const commentTableModel: CommentTableModel = createCommentTableModel();
    const instructorCommentTableModel: Record<string, CommentTableModel> = {
      'response-id': commentTableModel,
    };
    spyFeedbackResponseCommentService.createComment.mockReturnValue(
      throwError(() => ({ error: { message: errorMessage } })),
    );

    service.saveNewComment({
      responseId: 'response-id',
      timezone,
      instructorCommentTableModel,
    });

    expect(spyStatusMessageService.showErrorToast).toHaveBeenCalledWith(errorMessage);
    expect(instructorCommentTableModel['response-id'].commentRows).toHaveLength(0);
    expect(instructorCommentTableModel['response-id'].isAddingNewComment).toBe(true);
  });
});
