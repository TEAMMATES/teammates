import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { ResponseInstructorCommentService } from './feedback-response-comment.service';
import { InstructorCommentService } from './instructor-comment.service';
import { StatusMessageService } from './status-message.service';
import { TableComparatorService } from './table-comparator.service';
import createSpyFromClass from '../test-helpers/create-spy-from-class';
import { CommentVisibilityType, ResponseInstructorComment } from '../types/api-output';
import { SortBy, SortOrder } from '../types/sort-properties';
import type { InstructorCommentRowModel, NewCommentRowModel } from '../app/components/comment-box/comment.model';
import { CommentTableModel } from '../app/components/comment-box/comment-table/comment-table.model';

type Spy<T> = {
  [K in keyof T]: (...args: unknown[]) => unknown;
};

describe('InstructorCommentService', () => {
  const timezone = 'Asia/Singapore';
  const errorMessage = 'Something went wrong';

  let spyResponseInstructorCommentService: Spy<ResponseInstructorCommentService>;
  let spyStatusMessageService: Spy<StatusMessageService>;
  let spyTableComparatorService: Spy<TableComparatorService>;
  let service: InstructorCommentService;

  const createComment = (overrides: Partial<ResponseInstructorComment> = {}): ResponseInstructorComment => ({
    commentGiverName: 'Original Instructor',
    lastEditorName: 'Original Instructor',
    responseInstructorCommentId: 'comment-id',
    commentText: 'comment text',
    createdAt: 1000,
    lastEditedAt: 1000,
    showGiverNameTo: [CommentVisibilityType.INSTRUCTORS],
    showCommentTo: [CommentVisibilityType.INSTRUCTORS],
    ...overrides,
  });

  const createCommentRow = (comment: ResponseInstructorComment = createComment()): InstructorCommentRowModel => ({
    commentType: 'instructor',
    timezone,
    commentId: comment.responseInstructorCommentId,
    commentGiverName: comment.commentGiverName,
    lastEditorName: comment.lastEditorName,
    createdAt: comment.createdAt,
    lastEditedAt: comment.lastEditedAt,
    originalCommentFormModel: {
      commentText: comment.commentText,
      showCommentTo: comment.showCommentTo,
      showGiverNameTo: comment.showGiverNameTo,
    },
    commentEditFormModel: {
      commentText: comment.commentText,
      showCommentTo: comment.showCommentTo,
      showGiverNameTo: comment.showGiverNameTo,
    },
    isEditing: false,
  });

  const createNewCommentRow = (): NewCommentRowModel => ({
    commentType: 'new',
    commentEditFormModel: {
      commentText: 'new comment text',
      showCommentTo: [CommentVisibilityType.RECIPIENT],
      showGiverNameTo: [CommentVisibilityType.RECIPIENT],
    },
    isEditing: true,
  });

  const createCommentTableModel = (commentRows: InstructorCommentRowModel[] = []): CommentTableModel => ({
    commentRows,
    newCommentRow: createNewCommentRow(),
    isAddingNewComment: true,
    isReadOnly: false,
  });

  beforeEach(() => {
    spyResponseInstructorCommentService = createSpyFromClass(ResponseInstructorCommentService);
    spyStatusMessageService = createSpyFromClass(StatusMessageService);
    spyTableComparatorService = createSpyFromClass(TableComparatorService);
    spyTableComparatorService.compare.mockReturnValue(0);

    TestBed.configureTestingModule({
      providers: [
        { provide: ResponseInstructorCommentService, useValue: spyResponseInstructorCommentService },
        { provide: StatusMessageService, useValue: spyStatusMessageService },
        { provide: TableComparatorService, useValue: spyTableComparatorService },
      ],
    });
    service = TestBed.inject(InstructorCommentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should delete an instructor comment', () => {
    const commentTableModel: CommentTableModel = createCommentTableModel([
      createCommentRow(createComment({ responseInstructorCommentId: 'comment-id-1' })),
      createCommentRow(createComment({ responseInstructorCommentId: 'comment-id-2' })),
    ]);
    const instructorCommentTableModel: Record<string, CommentTableModel> = {
      'response-id': commentTableModel,
    };
    spyResponseInstructorCommentService.deleteComment.mockReturnValue(of({}));

    service.deleteComment({
      data: { responseId: 'response-id', index: 0 },
      instructorCommentTableModel,
    });

    expect(spyResponseInstructorCommentService.deleteComment).toHaveBeenCalledWith('comment-id-1');
    expect(instructorCommentTableModel['response-id'].commentRows).toHaveLength(1);
    expect(instructorCommentTableModel['response-id'].commentRows[0].commentId).toBe('comment-id-2');
  });

  it('should show error toast when deleting an instructor comment fails', () => {
    const commentTableModel: CommentTableModel = createCommentTableModel([createCommentRow()]);
    const instructorCommentTableModel: Record<string, CommentTableModel> = {
      'response-id': commentTableModel,
    };
    spyResponseInstructorCommentService.deleteComment.mockReturnValue(
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
    const originalComment: ResponseInstructorComment = createComment({
      responseInstructorCommentId: 'comment-id-to-update',
      commentText: 'old text',
    });
    const commentTableModel: CommentTableModel = createCommentTableModel([createCommentRow(originalComment)]);
    commentTableModel.commentRows[0].commentEditFormModel = {
      commentText: 'updated text',
      showCommentTo: [CommentVisibilityType.RECIPIENT],
      showGiverNameTo: [CommentVisibilityType.RECIPIENT],
    };
    const updatedComment: ResponseInstructorComment = createComment({
      responseInstructorCommentId: 'comment-id-to-update',
      commentText: 'updated text',
      createdAt: 1000,
      lastEditedAt: 2000,
      showCommentTo: [CommentVisibilityType.RECIPIENT],
      showGiverNameTo: [CommentVisibilityType.RECIPIENT],
    });
    const instructorCommentTableModel: Record<string, CommentTableModel> = {
      'response-id': commentTableModel,
    };
    spyResponseInstructorCommentService.updateComment.mockReturnValue(of(updatedComment));

    service.updateComment({
      data: { responseId: 'response-id', index: 0 },
      timezone,
      instructorCommentTableModel,
    });

    expect(spyResponseInstructorCommentService.updateComment).toHaveBeenCalledWith(
      {
        commentText: 'updated text',
        showCommentTo: [CommentVisibilityType.RECIPIENT],
        showGiverNameTo: [CommentVisibilityType.RECIPIENT],
      },
      'comment-id-to-update',
    );
    expect(instructorCommentTableModel['response-id'].commentRows[0].originalCommentFormModel?.commentText).toBe(
      'updated text',
    );
    expect(instructorCommentTableModel['response-id'].commentRows[0].timezone).toBe(timezone);
  });

  it('should show error toast when updating an instructor comment fails', () => {
    const commentTableModel: CommentTableModel = createCommentTableModel([createCommentRow()]);
    const instructorCommentTableModel: Record<string, CommentTableModel> = {
      'response-id': commentTableModel,
    };
    spyResponseInstructorCommentService.updateComment.mockReturnValue(
      throwError(() => ({ error: { message: errorMessage } })),
    );

    service.updateComment({
      data: { responseId: 'response-id', index: 0 },
      timezone,
      instructorCommentTableModel,
    });

    expect(spyStatusMessageService.showErrorToast).toHaveBeenCalledWith(errorMessage);
    expect(instructorCommentTableModel['response-id'].commentRows[0].originalCommentFormModel?.commentText).toBe(
      'comment text',
    );
  });

  it('should save a new instructor comment and keep comments sorted', () => {
    const oldComment: ResponseInstructorComment = createComment({
      responseInstructorCommentId: 'old-comment-id',
      commentText: 'old comment',
      createdAt: 2000,
    });
    const newComment: ResponseInstructorComment = createComment({
      responseInstructorCommentId: 'new-comment-id',
      commentText: 'new comment text',
      createdAt: 1000,
    });
    const commentTableModel: CommentTableModel = createCommentTableModel([createCommentRow(oldComment)]);
    const instructorCommentTableModel: Record<string, CommentTableModel> = {
      'response-id': commentTableModel,
    };
    spyResponseInstructorCommentService.createComment.mockReturnValue(of(newComment));
    spyTableComparatorService.compare.mockImplementation(
      (_sortBy: SortBy, _sortOrder: SortOrder, strA: string, strB: string) => Number(strA) - Number(strB),
    );

    service.saveNewComment({
      responseId: 'response-id',
      timezone,
      instructorCommentTableModel,
    });

    expect(spyResponseInstructorCommentService.createComment).toHaveBeenCalledWith(
      {
        commentText: 'new comment text',
        showCommentTo: [CommentVisibilityType.RECIPIENT],
        showGiverNameTo: [CommentVisibilityType.RECIPIENT],
      },
      'response-id',
    );
    expect(instructorCommentTableModel['response-id'].commentRows).toHaveLength(2);
    expect(instructorCommentTableModel['response-id'].commentRows[0].commentId).toBe('new-comment-id');
    expect(instructorCommentTableModel['response-id'].newCommentRow.commentEditFormModel.commentText).toBe('');
    expect(instructorCommentTableModel['response-id'].isAddingNewComment).toBe(false);
  });

  it('should show error toast when saving a new instructor comment fails', () => {
    const commentTableModel: CommentTableModel = createCommentTableModel();
    const instructorCommentTableModel: Record<string, CommentTableModel> = {
      'response-id': commentTableModel,
    };
    spyResponseInstructorCommentService.createComment.mockReturnValue(
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
