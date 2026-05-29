import { FeedbackResponseComment } from '../../../types/api-output';
import { CommentsToCommentTableModelPipe } from './comments-to-comment-table-model.pipe';
import { TestBed } from '@angular/core/testing';

describe('CommentsToCommentTableModelPipe', () => {
  let pipe: CommentsToCommentTableModelPipe;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CommentsToCommentTableModelPipe],
    });

    pipe = TestBed.inject(CommentsToCommentTableModelPipe);
  });

  it('converts comments to comment table model correctly', () => {
    const comments: FeedbackResponseComment[] = [
      {
        commentGiverName: 'commentGiverName',
        lastEditorName: 'lastEditorName',
        feedbackResponseCommentId: '00000000-0000-4000-8000-000000000000',
        commentText: 'commentText',
        createdAt: 0,
        lastEditedAt: 0,
        isVisibilityFollowingFeedbackQuestion: false,
        showGiverNameTo: [],
        showCommentTo: [],
      },
      {
        commentGiverName: 'commentGiverName2',
        lastEditorName: 'lastEditorName2',
        feedbackResponseCommentId: '00000000-0000-4000-8000-000000000001',
        commentText: 'commentText2',
        createdAt: 1,
        lastEditedAt: 1,
        isVisibilityFollowingFeedbackQuestion: true,
        showGiverNameTo: [],
        showCommentTo: [],
      },
    ];
    expect(pipe.transform(comments, true, 'UTC')).toEqual({
      commentRows: [
        {
          commentType: 'instructor',
          timezone: 'UTC',
          commentGiverName: 'commentGiverName',
          lastEditorName: 'lastEditorName',
          commentId: '00000000-0000-4000-8000-000000000000',
          createdAt: 0,
          lastEditedAt: 0,
          originalCommentFormModel: {
            commentText: 'commentText',
            isUsingCustomVisibilities: true,
            showCommentTo: [],
            showGiverNameTo: [],
          },
          commentEditFormModel: {
            commentText: 'commentText',
            isUsingCustomVisibilities: true,
            showCommentTo: [],
            showGiverNameTo: [],
          },
          isEditing: false,
        },
        {
          commentType: 'instructor',
          timezone: 'UTC',
          commentGiverName: 'commentGiverName2',
          lastEditorName: 'lastEditorName2',
          commentId: '00000000-0000-4000-8000-000000000001',
          createdAt: 1,
          lastEditedAt: 1,
          originalCommentFormModel: {
            commentText: 'commentText2',
            isUsingCustomVisibilities: false,
            showCommentTo: [],
            showGiverNameTo: [],
          },
          commentEditFormModel: {
            commentText: 'commentText2',
            isUsingCustomVisibilities: false,
            showCommentTo: [],
            showGiverNameTo: [],
          },
          isEditing: false,
        },
      ],
      newCommentRow: {
        commentType: 'new',
        commentEditFormModel: {
          commentText: '',
          isUsingCustomVisibilities: false,
          showCommentTo: [],
          showGiverNameTo: [],
        },
        isEditing: false,
      },
      isAddingNewComment: false,
      isReadOnly: true,
    });
  });
});
