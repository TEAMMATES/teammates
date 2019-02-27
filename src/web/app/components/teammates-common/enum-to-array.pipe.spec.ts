import { EnumToArrayPipe } from './enum-to-array.pipe';

describe('EnumToArrayPipe', () => {
  it('create an instance', () => {
    const pipe: EnumToArrayPipe = new EnumToArrayPipe();
    expect(pipe).toBeTruthy();
  });
});
