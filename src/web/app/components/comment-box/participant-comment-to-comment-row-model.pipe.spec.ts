import { ParticipantCommentToCommentRowModelPipe } from './participant-comment-to-comment-row-model.pipe';

describe('ParticipantCommentToCommentRowModelPipe', () => {
  it('create an instance', () => {
    const pipe: ParticipantCommentToCommentRowModelPipe = new ParticipantCommentToCommentRowModelPipe();
    expect(pipe).toBeTruthy();
  });
});
