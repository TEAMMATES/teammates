import { FormatPhotoUrlPipe } from './format-photo-url.pipe';

describe('FormatPhotoUrlPipe', () => {
  it('create an instance', () => {
    const pipe: FormatPhotoUrlPipe = new FormatPhotoUrlPipe();
    expect(pipe).toBeTruthy();
  });
});
