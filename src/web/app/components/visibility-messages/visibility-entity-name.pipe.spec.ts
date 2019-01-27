import { VisibilityEntityNamePipe } from './visibility-entity-name.pipe';

describe('VisibilityEntityNamePipe', () => {
  it('create an instance', () => {
    const pipe: VisibilityEntityNamePipe = new VisibilityEntityNamePipe();
    expect(pipe).toBeTruthy();
  });
});
