import { ParticipantCommentToCommentRowModelPipe } from './participant-comment-to-comment-row-model.pipe';

describe('ParticipantCommentToCommandRowModelPipe', () => {
  it('create an instance', () => {
    const pipe: ParticipantCommentToCommentRowModelPipe = new ParticipantCommentToCommentRowModelPipe();
    expect(pipe).toBeTruthy();
  });
});
