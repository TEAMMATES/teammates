import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'highlighter',
})
export class SearchTermsHighlighterPipe implements PipeTransform {

    /**
     * Transforms text to add highlighting styling.
     *
     * @param value text to be transformed if it contains search terms
     * @param searchStr search terms entered by user in the search bar
     * @param isPartialMatch optional argument, true when text should be highlighted on a partial match,
     *                by default, text is only highlighted on a full word match
     * @returns transformed text with styling added if search terms were found
     */
    transform(value: string, searchStr: string, isPartialMatch?: boolean): string {
        if (!searchStr) {
            return value;
        }
        const exactPhrases: string = this.findAllExactPhrases(searchStr).map((str) => {
            return isPartialMatch ? `${str}` : `\\b${str}\\b`;
        })
        .filter((str) => str !== '\\b\\b').join('|');
        const searchTerms: string = this.removeAllExactPhrases(searchStr);

        let result: string = value;

        if (searchTerms.trim() !== '') {
            const combinedSearchTerms = searchTerms.split(' ').map((str) => {
                return isPartialMatch ? `${str}` : `\\b${str}\\b`;
            })
            .filter((str) => str !== '\\b\\b').join('|');
            const partialMatchRe = new RegExp(combinedSearchTerms, 'igm');
            result = result.replace(partialMatchRe, '<span class="highlighted-text">$&</span>');
        }

        if (exactPhrases.trim() !== '') {
            const exactPhrasesRe = new RegExp(exactPhrases, 'igm');
            result = result.replace(exactPhrasesRe, '<span class="highlighted-text">$&</span>');
        }

        return result;
    }

    checkIsExactPhrase(term: string): boolean {
        return term.charAt(0) === '"' && term.charAt(term.length - 1) === '"';
    }

    findAllExactPhrases(searchValue: string): string[] {
        const exactPhrases = searchValue.match(/"(.*?)"/igm);
        if (exactPhrases === null) {
            return [];
        }
        return exactPhrases.map((regexp) => {
            const str = regexp.toString();
            return str.substring(1, str.length - 1);
        });
    }

    removeAllExactPhrases(searchValue: string): string {
        const cleanedString = searchValue.replace(/"(.*?)"/igm, '').trim();
        return cleanedString;
    }

}
