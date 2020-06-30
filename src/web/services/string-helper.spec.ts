import { StringHelper } from './string-helper';

describe('getTextFromHtml', () => {
  it('should return the original sting if it is not HTML string', () => {
    expect(StringHelper.getTextFromHtml('I am cool.')).toEqual('I am cool.');
  });

  it('should return the string content in the HTML tag', () => {
    expect(StringHelper.getTextFromHtml('<p>I am cool.</p>')).toEqual('I am cool.');
  });

  it('should handle properly for malformed HTML', () => {
    expect(StringHelper.getTextFromHtml('<p>I am cool.')).toEqual('I am cool.');
  });

  it('should replace line breaks \\n', () => {
    expect(StringHelper.getTextFromHtml('<p>I am cool.\n multi-line'))
        .toEqual('I am cool.  multi-line');
  });

  it('should replace line breaks \\n\\r', () => {
    expect(StringHelper.getTextFromHtml('<p>I am cool.\r\n multi-line <\p>'))
        .toEqual('I am cool.  multi-line ');
  });
});

describe('convertImageToLinkInHtml', () => {
  it('should return empty string if empty string is passed', () => {
    expect(StringHelper.convertImageToLinkInHtml('')).toEqual('');
  });

  it('should return empty string if no img tags are present', () => {
    expect(StringHelper.convertImageToLinkInHtml('<h1>header one</h1>')).toEqual('');
  });

  it('should return image links in img tags', () => {
    expect(StringHelper.convertImageToLinkInHtml('<img src="http://www.example.com/test.jpg">Bob</img>'))
        .toEqual(' Images Link: http://www.example.com/test.jpg ');
  });

  it('should return empty string for malformed img tags', () => {
    expect(StringHelper.convertImageToLinkInHtml('Bob</img>')).toEqual('');
  });
});

describe('removeExtraSpace', () => {
  it('should return empty string for empty string', () => {
    expect(StringHelper.removeExtraSpace('')).toEqual('');
  });

  it('should also remove spaces inside string', () => {
    expect(StringHelper.removeExtraSpace('a    a')).toEqual('a a');
  });

  it('should handle properly for Unicode string', () => {
    expect(StringHelper.removeExtraSpace(' \u00A0 a    a   ')).toEqual('a a');
  });

  it('should handle properly for multiple spaces', () => {
    expect(StringHelper.removeExtraSpace('    ')).toEqual('');
  });

  it('should remove extra spaces', () => {
    expect(StringHelper.removeExtraSpace(' a      b       c       d      ')).toEqual('a b c d');
  });
});

describe('integerToLowerCaseAlphabeticalIndex', () => {
  it('should return empty string for negative input', () => {
    expect(StringHelper.integerToLowerCaseAlphabeticalIndex(-1)).toEqual('');
  });

  it('should return empty string for zero input', () => {
    expect(StringHelper.integerToLowerCaseAlphabeticalIndex(0)).toEqual('');
  });

  it('should return alphabetical index for index 1', () => {
    expect(StringHelper.integerToLowerCaseAlphabeticalIndex(1)).toEqual('a');
  });

  it('should return alphabetical index for index 26', () => {
    expect(StringHelper.integerToLowerCaseAlphabeticalIndex(26)).toEqual('z');
  });

  it('should return alphabetical index for index 27', () => {
    expect(StringHelper.integerToLowerCaseAlphabeticalIndex(27)).toEqual('aa');
  });

  it('should return alphabetical index for index 100', () => {
    expect(StringHelper.integerToLowerCaseAlphabeticalIndex(100)).toEqual('vc');
  });
});
