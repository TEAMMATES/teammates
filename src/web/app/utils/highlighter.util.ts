export function textToHighlighting(value: string, searchStr: string, isPartialMatch?: boolean): string {
  if (!value || !searchStr) {
    return value;
  }
  const exactPhrases: string = findAllExactPhrases(searchStr)
    .map((str) => {
      const escapedPhrase: string = escapeRegExp(str);
      return isPartialMatch ? escapedPhrase : `\\b${escapedPhrase}\\b`;
    })
    .filter((str) => str !== '\\b\\b')
    .join('|');
  const searchTerms: string = removeAllExactPhrases(searchStr);

  let result: string = value;

  if (searchTerms.trim() !== '') {
    const combinedSearchTerms = searchTerms
      .split(' ')
      .map((str) => {
        const escapedTerm: string = escapeRegExp(str);
        return isPartialMatch ? escapedTerm : `\\b${escapedTerm}\\b`;
      })
      .filter((str) => str !== '\\b\\b')
      .join('|');
    const partialMatchRe = new RegExp(combinedSearchTerms, 'igm');
    result = result.replace(partialMatchRe, '<span class="highlighted-text">$&</span>');
  }

  if (exactPhrases.trim() !== '') {
    const exactPhrasesRe = new RegExp(exactPhrases, 'igm');
    result = result.replace(exactPhrasesRe, '<span class="highlighted-text">$&</span>');
  }

  return result;
}

function findAllExactPhrases(searchValue: string): string[] {
  const exactPhrases = searchValue.match(/"(.*?)"/gim);
  if (exactPhrases === null) {
    return [];
  }
  return exactPhrases.map((regexp) => {
    const str = regexp.toString();
    return str.substring(1, str.length - 1);
  });
}

function removeAllExactPhrases(searchValue: string): string {
  return searchValue.replace(/"(.*?)"/gim, '').trim();
}

function escapeRegExp(value: string): string {
  return value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}
