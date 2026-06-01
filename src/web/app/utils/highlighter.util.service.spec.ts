import { HighlighterUtil } from './highlighter.util.service';

describe('HighlighterUtil', () => {
  let util: HighlighterUtil;

  beforeEach(() => {
    util = new HighlighterUtil();
  });

  describe('searchTermsHighlighter', () => {
    it('should return value unchanged when value is empty', () => {
      expect(util.searchTermsHighlighter('', 'term')).toBe('');
    });

    it('should return value unchanged when searchStr is empty', () => {
      expect(util.searchTermsHighlighter('hello world', '')).toBe('hello world');
    });

    it('should highlight a single word (whole word match)', () => {
      expect(util.searchTermsHighlighter('hello world', 'hello')).toBe(
        '<span class="highlighted-text">hello</span> world',
      );
    });

    it('should not highlight partial word by default', () => {
      expect(util.searchTermsHighlighter('helloworld', 'hello')).toBe('helloworld');
    });

    it('should highlight partial word when isPartialMatch is true', () => {
      expect(util.searchTermsHighlighter('helloworld', 'hello', true)).toBe(
        '<span class="highlighted-text">hello</span>world',
      );
    });

    it('should highlight an exact phrase in quotes', () => {
      expect(util.searchTermsHighlighter('hello world foo', '"hello world"')).toBe(
        '<span class="highlighted-text">hello world</span> foo',
      );
    });

    it('should highlight multiple words', () => {
      const result = util.searchTermsHighlighter('foo bar baz', 'foo baz');
      expect(result).toContain('<span class="highlighted-text">foo</span>');
      expect(result).toContain('<span class="highlighted-text">baz</span>');
    });

    it('should be case-insensitive', () => {
      expect(util.searchTermsHighlighter('Hello World', 'hello')).toBe(
        '<span class="highlighted-text">Hello</span> World',
      );
    });
  });

  describe('checkIsExactPhrase', () => {
    it('should return true for quoted string', () => {
      expect(util.checkIsExactPhrase('"hello world"')).toBe(true);
    });

    it('should return false for unquoted string', () => {
      expect(util.checkIsExactPhrase('hello world')).toBe(false);
    });
  });

  describe('findAllExactPhrases', () => {
    it('should return empty array when no quoted phrases', () => {
      expect(util.findAllExactPhrases('hello world')).toEqual([]);
    });

    it('should extract quoted phrases', () => {
      expect(util.findAllExactPhrases('"hello world" foo "bar baz"')).toEqual(['hello world', 'bar baz']);
    });
  });

  describe('removeAllExactPhrases', () => {
    it('should remove quoted phrases and trim', () => {
      expect(util.removeAllExactPhrases('"hello world" foo "bar"')).toBe('foo');
    });

    it('should return original string when no quoted phrases', () => {
      expect(util.removeAllExactPhrases('hello world')).toBe('hello world');
    });
  });
});
