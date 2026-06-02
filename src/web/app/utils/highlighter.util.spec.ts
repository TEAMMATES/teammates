import { textToHighlighting } from './highlighter.util';

describe('highlighter', () => {
  describe('textToHighlighting', () => {
    it('should return value unchanged when value is empty', () => {
      expect(textToHighlighting('', 'term')).toBe('');
    });

    it('should return value unchanged when searchStr is empty', () => {
      expect(textToHighlighting('hello world', '')).toBe('hello world');
    });

    it('should highlight a single word (whole word match)', () => {
      expect(textToHighlighting('hello world', 'hello')).toBe('<span class="highlighted-text">hello</span> world');
    });

    it('should not highlight partial word by default', () => {
      expect(textToHighlighting('helloworld', 'hello')).toBe('helloworld');
    });

    it('should highlight partial word when isPartialMatch is true', () => {
      expect(textToHighlighting('helloworld', 'hello', true)).toBe('<span class="highlighted-text">hello</span>world');
    });

    it('should highlight an exact phrase in quotes', () => {
      expect(textToHighlighting('hello world foo', '"hello world"')).toBe(
        '<span class="highlighted-text">hello world</span> foo',
      );
    });

    it('should highlight multiple words', () => {
      const result = textToHighlighting('foo bar baz', 'foo baz');
      expect(result).toContain('<span class="highlighted-text">foo</span>');
      expect(result).toContain('<span class="highlighted-text">baz</span>');
    });

    it('should be case-insensitive', () => {
      expect(textToHighlighting('Hello World', 'hello')).toBe('<span class="highlighted-text">Hello</span> World');
    });
  });
});
