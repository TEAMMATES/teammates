import { RecipientTypeNamePipe } from './recipient-type-name.pipe';

describe('RecipientTypeNamePipe', () => {
  it('create an instance', () => {
    const pipe: RecipientTypeNamePipe = new RecipientTypeNamePipe();
    expect(pipe).toBeTruthy();
  });
});
