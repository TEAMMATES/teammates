import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'highlighter'
})
export class SearchTermsHighlighterPipe implements PipeTransform {

    transform(value: any, args: any, type: string): unknown {
        if (!args) return value;
        const exactPhrases: string[] = this.findAllExactPhrases(args);
        args = this.removeAllExactPhrases(args);
        const terms = args.split(" ");
        for (const arg of terms) {
            if (arg !== "") {
                if (type === 'full') {
                    const re = new RegExp("\\b" + arg + "\\b", 'igm');
                    value = value.replace(re, '<span class="highlighted-text">$&</span>');
                }
                else {
                    const re = new RegExp(arg, 'igm');
                    value = value.replace(re, '<span class="highlighted-text">$&</span>');
                }
            }   
        }

        for (const exactPhrase of exactPhrases) {
            const re = new RegExp("\\b" + exactPhrase + "\\b", 'igm');
            value = value.replace(re, '<span class="highlighted-text">$&</span>');
        }

        return value;
    }

    checkIsExactPhrase(term: string): boolean {
        return term.charAt(0) === "\"" && term.charAt(term.length - 1) === '\"';
    }

    findAllExactPhrases(searchValue: string): string[] {
        const re = new RegExp('"(.*?)"', 'igm');
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
        const re = new RegExp('"(.*?)"', 'igm');
        searchValue = searchValue.replace(re, "");
        return searchValue
    }

}
