import { PublishStatusNamePipe } from './publish-status-name.pipe';

describe('PublishStatusNamePipe', () => {
  it('create an instance', () => {
    const pipe: PublishStatusNamePipe = new PublishStatusNamePipe();
    expect(pipe).toBeTruthy();
  });
});
