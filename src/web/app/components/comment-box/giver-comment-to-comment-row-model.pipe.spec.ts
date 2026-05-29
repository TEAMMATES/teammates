import { GiverCommentToCommentRowModelPipe } from './giver-comment-to-comment-row-model.pipe';

describe('GiverCommentToCommentRowModelPipe', () => {
  it('converts a giver comment string to a comment row model', () => {
    const pipe: GiverCommentToCommentRowModelPipe = new GiverCommentToCommentRowModelPipe();

    expect(pipe.transform('participant comment')).toEqual({
      commentType: 'giver',
      originalCommentFormModel: {
        commentText: 'participant comment',
        showCommentTo: ['GIVER'],
        showGiverNameTo: ['GIVER'],
      },
      commentEditFormModel: {
        commentText: 'participant comment',
        showCommentTo: ['GIVER'],
        showGiverNameTo: ['GIVER'],
      },
      isEditing: false,
    });
  });
});
