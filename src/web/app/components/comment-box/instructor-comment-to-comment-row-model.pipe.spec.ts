import { InstructorCommentToCommentRowModelPipe } from './instructor-comment-to-comment-row-model.pipe';
import { CommentVisibilityType, FeedbackResponseComment } from '../../../types/api-output';

describe('InstructorCommentToCommentRowModelPipe', () => {
  it('converts a feedback response comment to a comment row model', () => {
    const pipe: InstructorCommentToCommentRowModelPipe = new InstructorCommentToCommentRowModelPipe();
    const comment: FeedbackResponseComment = {
      feedbackResponseCommentId: 'comment-id',
      commentGiverName: 'Instructor',
      lastEditorName: 'Editor',
      commentText: 'comment text',
      createdAt: 1,
      lastEditedAt: 2,
      isVisibilityFollowingFeedbackQuestion: false,
      showCommentTo: [CommentVisibilityType.RECIPIENT],
      showGiverNameTo: [CommentVisibilityType.GIVER],
    };

    expect(pipe.transform(comment, 'UTC')).toEqual({
      commentType: 'instructor',
      timezone: 'UTC',
      commentId: 'comment-id',
      commentGiverName: 'Instructor',
      lastEditorName: 'Editor',
      createdAt: 1,
      lastEditedAt: 2,
      originalCommentFormModel: {
        commentText: 'comment text',
        isUsingCustomVisibilities: true,
        showCommentTo: [CommentVisibilityType.RECIPIENT],
        showGiverNameTo: [CommentVisibilityType.GIVER],
      },
      commentEditFormModel: {
        commentText: 'comment text',
        isUsingCustomVisibilities: true,
        showCommentTo: [CommentVisibilityType.RECIPIENT],
        showGiverNameTo: [CommentVisibilityType.GIVER],
      },
      isEditing: false,
    });
  });
});
