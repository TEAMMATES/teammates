import { CommentOutput } from '../../../types/api-output';
import { CommentToCommentRowModelPipe } from './comment-to-comment-row-model.pipe';
import { CommentsToCommentTableModelPipe } from './comments-to-comment-table-model.pipe';

describe('CommentsToCommentTableModelPipe', () => {
  it('converts comments to comment table model correctly', () => {
    const pipe: CommentsToCommentTableModelPipe = new CommentsToCommentTableModelPipe(
        new CommentToCommentRowModelPipe());
    const comments: CommentOutput[] = [
      {
        commentGiverName: 'commentGiverName',
        lastEditorName: 'lastEditorName',
        commentGiver: 'commentGiver',
        lastEditorEmail: 'lastEditorEmail',
        feedbackResponseCommentId: 0,
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
        commentGiver: 'commentGiver2',
        lastEditorEmail: 'lastEditorEmail2',
        feedbackResponseCommentId: 1,
        commentText: 'commentText2',
        createdAt: 1,
        lastEditedAt: 1,
        isVisibilityFollowingFeedbackQuestion: true,
        showGiverNameTo: [],
        showCommentTo: [],
      },
    ];
    expect(pipe.transform(comments, true, 'UTC')).toEqual({
      commentRows: [{
        timezone: 'UTC',
        originalComment: {
          commentGiverName: 'commentGiverName',
          lastEditorName: 'lastEditorName',
          commentGiver: 'commentGiver',
          lastEditorEmail: 'lastEditorEmail',
          feedbackResponseCommentId: 0,
          commentText: 'commentText',
          createdAt: 0,
          lastEditedAt: 0,
          isVisibilityFollowingFeedbackQuestion: false,
          showGiverNameTo: [],
          showCommentTo: [],
        },
        commentGiverName: 'commentGiverName',
        lastEditorName: 'lastEditorName',
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
          commentGiver: 'commentGiver2',
          lastEditorEmail: 'lastEditorEmail2',
          feedbackResponseCommentId: 1,
          commentText: 'commentText2',
          createdAt: 1,
          lastEditedAt: 1,
          isVisibilityFollowingFeedbackQuestion: true,
          showGiverNameTo: [],
          showCommentTo: [],
        },
        commentGiverName: 'commentGiverName2',
        lastEditorName: 'lastEditorName2',
        commentEditFormModel: {
          commentText: 'commentText2',
          isUsingCustomVisibilities: false,
          showCommentTo: [],
          showGiverNameTo: [],
        },
        isEditing: false,
      }],
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
