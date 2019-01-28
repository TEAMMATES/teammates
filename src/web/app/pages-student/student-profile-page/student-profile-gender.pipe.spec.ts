import { GenderFormatPipe } from './student-profile-gender.pipe';

describe('GenderFormatPipe', () => {
  it('create an instance', () => {
    const pipe: GenderFormatPipe = new GenderFormatPipe();
    expect(pipe).toBeTruthy();
  });
});
