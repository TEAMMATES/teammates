import { inject, Pipe, PipeTransform } from '@angular/core';
import { HighlighterUtil } from '../utils/highlighter.util.service';

@Pipe({ name: 'highlighter' })
export class SearchTermsHighlighterPipe implements PipeTransform {
  private highlighterUtil = inject(HighlighterUtil);

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
    return this.highlighterUtil.searchTermsHighlighter(value, searchStr, isPartialMatch);
  }
}
