import { TimeDisplayerPipe } from './time-displayer.pipe';

describe('TimeDisplayerPipe', () => {
  it('create an instance', () => {
    const pipe: TimeDisplayerPipe = new TimeDisplayerPipe();
    expect(pipe).toBeTruthy();
  });
});
