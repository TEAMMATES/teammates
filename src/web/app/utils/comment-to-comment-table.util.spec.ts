import { commentToReadOnlyComment } from './comment-to-comment-table.util';
import { ResponseInstructorComment } from '../../types/api-output';

const mockComment: ResponseInstructorComment = {
  responseInstructorCommentId: 'c1',
  commentGiverName: 'Instructor A',
  commentText: 'Good job',
  createdAt: 1000,
};

describe('commentToReadOnlyComment', () => {
  it('should set isReadOnly and isAddingNewComment correctly', () => {
    const result = commentToReadOnlyComment([mockComment], true, 'UTC');

    expect(result.isReadOnly).toBe(true);
    expect(result.isAddingNewComment).toBe(false);
  });

  it('should map each comment using instructorCommentToCommentRowModel', () => {
    const result = commentToReadOnlyComment([mockComment], false, 'Asia/Singapore');

    expect(result.commentRows).toEqual([
      {
        commentType: 'instructor',
        timezone: 'Asia/Singapore',
        commentGiverName: 'Instructor A',
        commentId: 'c1',
        createdAt: 1000,
        originalCommentFormModel: { commentText: 'Good job' },
        commentEditFormModel: { commentText: 'Good job' },
        isEditing: false,
      },
    ]);
  });

  it('should create an empty newCommentRow', () => {
    const result = commentToReadOnlyComment([], false, 'UTC');

    expect(result.newCommentRow).toEqual({
      commentType: 'new',
      commentEditFormModel: { commentText: '' },
      isEditing: false,
    });
  });
});
