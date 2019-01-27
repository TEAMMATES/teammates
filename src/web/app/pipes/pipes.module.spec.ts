import { Pipes } from './pipes.module';

describe('Pipes', () => {
  let pipes: Pipes;

  beforeEach(() => {
    pipes = new Pipes;
  });

  it('should be instantiated', () => {
    expect(Pipes).toBeTruthy();
  });
});
