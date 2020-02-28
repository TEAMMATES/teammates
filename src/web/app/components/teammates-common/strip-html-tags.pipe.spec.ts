import { StripHtmlTagsPipe } from './strip-html-tags.pipe';

describe('StripHtmlTagsPipe', () => {
  it('create an instance', () => {
    const pipe: StripHtmlTagsPipe = new StripHtmlTagsPipe();
    expect(pipe).toBeTruthy();
  });
});
