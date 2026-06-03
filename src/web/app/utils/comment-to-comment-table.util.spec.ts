import { commentToReadOnlyComment } from './comment-to-comment-table.util';
import { CommentVisibilityType, FeedbackVisibilityType, ResponseInstructorComment } from '../../types/api-output';

const mockComment: ResponseInstructorComment = {
  responseInstructorCommentId: 'c1',
  commentGiverName: 'Instructor A',
  lastEditorName: 'Instructor A',
  commentText: 'Good job',
  createdAt: 1000,
  lastEditedAt: 2000,
  showGiverNameTo: [],
  showCommentTo: [],
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
        lastEditorName: 'Instructor A',
        commentId: 'c1',
        createdAt: 1000,
        lastEditedAt: 2000,
        originalCommentFormModel: { commentText: 'Good job', showCommentTo: [], showGiverNameTo: [] },
        commentEditFormModel: { commentText: 'Good job', showCommentTo: [], showGiverNameTo: [] },
        isEditing: false,
      },
    ]);
  });

  it('should call createNewCommentRowModel with questionShowResponsesTo', () => {
    const visibilityTypes = [FeedbackVisibilityType.INSTRUCTORS];
    const result = commentToReadOnlyComment([], false, 'UTC', visibilityTypes);

    expect(result.newCommentRow).toEqual({
      commentType: 'new',
      commentEditFormModel: {
        commentText: '',
        showCommentTo: [CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS],
        showGiverNameTo: [CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS],
      },
      isEditing: false,
    });
  });

  it('should default questionShowResponsesTo to empty array', () => {
    const result = commentToReadOnlyComment([], false, 'UTC');

    expect(result.newCommentRow).toEqual({
      commentType: 'new',
      commentEditFormModel: { commentText: '', showCommentTo: ['GIVER'], showGiverNameTo: ['GIVER'] },
      isEditing: false,
    });
  });
});
