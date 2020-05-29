import { ResponseToReadonlyCommentTableModelPipe } from './response-to-readonly-comment-table-model.pipe';

describe('ResponseToReadonlyCommentTableModelPipe', () => {
  it('create an instance', () => {
    const pipe: ResponseToReadonlyCommentTableModelPipe = new ResponseToReadonlyCommentTableModelPipe();
    expect(pipe).toBeTruthy();
  });
});
