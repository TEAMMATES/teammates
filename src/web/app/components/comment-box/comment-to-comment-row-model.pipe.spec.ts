import { CommentToCommentRowModelPipe } from './comment-to-comment-row-model.pipe';

describe('CommentToCommentRowModelPipe', () => {
  it('create an instance', () => {
    const pipe: CommentToCommentRowModelPipe = new CommentToCommentRowModelPipe();
    expect(pipe).toBeTruthy();
  });
});
