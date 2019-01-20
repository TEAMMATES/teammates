import { SubmissionStatusNamePipe } from './submission-status-name.pipe';

describe('SubmissionStatusNamePipe', () => {
  it('create an instance', () => {
    const pipe: SubmissionStatusNamePipe = new SubmissionStatusNamePipe();
    expect(pipe).toBeTruthy();
  });
});
