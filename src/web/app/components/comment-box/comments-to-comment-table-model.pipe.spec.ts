import { ResponseInstructorComment } from '../../../types/api-output';
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
    const comments: ResponseInstructorComment[] = [
      {
        commentGiverName: 'commentGiverName',
        responseInstructorCommentId: '00000000-0000-4000-8000-000000000000',
        commentText: 'commentText',
        createdAt: 0,
      },
      {
        commentGiverName: 'commentGiverName2',
        responseInstructorCommentId: '00000000-0000-4000-8000-000000000001',
        commentText: 'commentText2',
        createdAt: 1,
      },
    ];
    expect(pipe.transform(comments, true, 'UTC')).toEqual({
      commentRows: [
        {
          commentType: 'instructor',
          timezone: 'UTC',
          commentGiverName: 'commentGiverName',
          commentId: '00000000-0000-4000-8000-000000000000',
          createdAt: 0,
          originalCommentFormModel: {
            commentText: 'commentText',
          },
          commentEditFormModel: {
            commentText: 'commentText',
          },
          isEditing: false,
        },
        {
          commentType: 'instructor',
          timezone: 'UTC',
          commentGiverName: 'commentGiverName2',
          commentId: '00000000-0000-4000-8000-000000000001',
          createdAt: 1,
          originalCommentFormModel: {
            commentText: 'commentText2',
          },
          commentEditFormModel: {
            commentText: 'commentText2',
          },
          isEditing: false,
        },
      ],
      newCommentRow: {
        commentType: 'new',
        commentEditFormModel: {
          commentText: '',
        },
        isEditing: false,
      },
      isAddingNewComment: false,
      isReadOnly: true,
    });
  });
});
