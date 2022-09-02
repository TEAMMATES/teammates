import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'highlighter',
})
export class SearchTermsHighlighterPipe implements PipeTransform {

    transform(value: any, args: any): unknown {
        if (!args) return value;
        const exactPhrases: string = this.findAllExactPhrases(args).map((str) => `\\b${str}\\b`)
        .filter((str) => str !== '\\b\\b').join('|');
        const searchTerms: string = this.removeAllExactPhrases(args);

        let result:string = value;

        if (searchTerms.trim() !== '') {
            const combinedSearchTerms = searchTerms.split(' ').map((str) => `\\b${str}\\b`)
            .filter((str) => str !== '\\b\\b').join('|');
            const partialMatchRe = new RegExp(combinedSearchTerms, 'igm');
            result = value.replace(partialMatchRe, '<span class="highlighted-text">$&</span>');
        }
        if (exactPhrases.trim() !== '') {
            const exactPhrasesRe = new RegExp(exactPhrases, 'igm');
            result = value.replace(exactPhrasesRe, '<span class="highlighted-text">$&</span>');
        }

        return result;
    }

    checkIsExactPhrase(term: string): boolean {
        return term.charAt(0) === '"' && term.charAt(term.length - 1) === '"';
    }

    findAllExactPhrases(searchValue: string): string[] {
        const re = /"(.*?)"/igm;
        const exactPhrases = searchValue.match(re);
        if (exactPhrases === null) {
            return [];
        }
        return exactPhrases.map((regexp) => {
            const str = regexp.toString();
            return str.substring(1, str.length - 1);
        });
    }

    removeAllExactPhrases(searchValue: string): string {
        const re = /'"(.*?)"/igm;
        const cleanedString = searchValue.replace(re, '');
        return cleanedString;
    }

}
