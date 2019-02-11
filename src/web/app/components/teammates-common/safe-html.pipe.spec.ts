import { SafeHtmlPipe } from './safe-html.pipe';

describe('SafeHtmlPipe', () => {
  it('create an instance', () => {
    const pipe: SafeHtmlPipe = new SafeHtmlPipe();
    expect(pipe).toBeTruthy();
  });
});
