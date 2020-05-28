import { ResponseOutputToReadonlyCommentTableModelPipe } from './response-output-to-readonly-comment-table-model.pipe';

describe('ResponseOutputToReadonlyCommentTableModelPipe', () => {
  it('create an instance', () => {
    const pipe: ResponseOutputToReadonlyCommentTableModelPipe = new ResponseOutputToReadonlyCommentTableModelPipe();
    expect(pipe).toBeTruthy();
  });
});
