import { GiverTypeDescriptionPipe, RecipientTypeDescriptionPipe } from './feedback-path.pipe';

describe('GiverTypeDescriptionPipe', () => {
  it('create an instance', () => {
    const pipe: GiverTypeDescriptionPipe = new GiverTypeDescriptionPipe();
    expect(pipe).toBeTruthy();
  });
});

describe('RecipientTypeDescriptionPipe', () => {
  it('create an instance', () => {
    const pipe: RecipientTypeDescriptionPipe = new RecipientTypeDescriptionPipe();
    expect(pipe).toBeTruthy();
  });
});
