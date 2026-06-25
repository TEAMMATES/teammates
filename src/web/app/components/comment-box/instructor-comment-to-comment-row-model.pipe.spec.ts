import { InstructorCommentToCommentRowModelPipe } from './instructor-comment-to-comment-row-model.pipe';
import { ResponseInstructorComment } from '../../../types/api-output';

describe('InstructorCommentToCommentRowModelPipe', () => {
  it('converts a feedback response comment to a comment row model', () => {
    const pipe: InstructorCommentToCommentRowModelPipe = new InstructorCommentToCommentRowModelPipe();
    const comment: ResponseInstructorComment = {
      responseInstructorCommentId: 'comment-id',
      commentGiverName: 'Instructor',
      commentText: 'comment text',
      createdAt: 1,
    };

    expect(pipe.transform(comment, 'UTC')).toEqual({
      commentType: 'instructor',
      timezone: 'UTC',
      commentId: 'comment-id',
      commentGiverName: 'Instructor',
      createdAt: 1,
      originalCommentFormModel: {
        commentText: 'comment text',
      },
      commentEditFormModel: {
        commentText: 'comment text',
      },
      isEditing: false,
    });
  });
});
