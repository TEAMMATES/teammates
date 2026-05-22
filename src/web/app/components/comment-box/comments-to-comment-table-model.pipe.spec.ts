import { FeedbackResponseComment } from '../../../types/api-output';
import { CommentToCommentRowModelPipe } from './comment-to-comment-row-model.pipe';
import { CommentsToCommentTableModelPipe } from './comments-to-comment-table-model.pipe';
import { TestBed } from '@angular/core/testing';

describe('CommentsToCommentTableModelPipe', () => {
  let pipe: CommentsToCommentTableModelPipe;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CommentsToCommentTableModelPipe, CommentToCommentRowModelPipe],
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
          timezone: 'UTC',
          originalComment: {
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
          commentEditFormModel: {
            commentText: 'commentText',
            isUsingCustomVisibilities: true,
            showCommentTo: [],
            showGiverNameTo: [],
          },
          isEditing: false,
        },
        {
          timezone: 'UTC',
          originalComment: {
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
