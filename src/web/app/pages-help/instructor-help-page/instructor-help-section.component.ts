import { DOCUMENT } from '@angular/common';
import {
  AfterViewInit, Directive, EventEmitter, Inject, Input, OnChanges, OnInit, Output, QueryList,
  SimpleChanges, ViewChildren,
} from '@angular/core';
import { PageScrollService } from 'ngx-page-scroll-core';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { InstructorHelpPanelComponent } from './instructor-help-panel/instructor-help-panel.component';

interface QuestionDetail {
  id: string;
  text: string;
  keywords: string[];
}

/**
 * Base section for instructor help page.
 */
@Directive()
// tslint:disable-next-line:directive-class-suffix
export abstract class InstructorHelpSectionComponent implements OnInit, OnChanges, AfterViewInit {

  @Input() key: String;
  @Output() matchFound: EventEmitter<number> = new EventEmitter<number>();
  @ViewChildren('question') questionHtml!: QueryList<InstructorHelpPanelComponent>;

  showQuestion: string[];
  questionDetails: QuestionDetail[];
  questionsToCollapsed: Record<string, boolean> = {};

  constructor(protected simpleModalService: SimpleModalService,
              private pageScrollService: PageScrollService,
              @Inject(DOCUMENT) private document: any) {
    this.key = '';
    this.showQuestion = [];
    this.questionDetails = [];
  }

  ngOnInit(): void {
    for (const question of this.getQuestionsOrder()) {
      this.questionsToCollapsed[question] = false;
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!changes.key) {
      return;
    }
    // Collapse all questions when new search is initiated
    for (const questionId of Object.keys(this.questionsToCollapsed)) {
      this.questionsToCollapsed[questionId] = false;
    }
    if (this.key === '') {
      this.resetFaq();
    } else {
      this.filterFaq(changes.key.currentValue);
    }
  }

  /**
   * Retrieves HTML components from DOM after its rendered.
   */
  ngAfterViewInit(): void {
    if (this.questionDetails.length === 0) {
      this.generateTerms();
    }
  }

  private generateTerms(): void {
    this.questionHtml.forEach((question: InstructorHelpPanelComponent) => {
      const id: string = question.id;
      const text: string = (question.elementRef.nativeElement.textContent || '').toLowerCase();

        // filter small words away
      let keywords: string[] = text.split(' ').filter((word: string) => word.length > 3);

        // remove punctuation
      keywords = keywords.map((word: string) =>
        word.replace(/\b[-.,()?&$#!\[\]{}']+\B|\B[-.,()&?$#!\[\]{}']+\b/g, ''));

      const newQuestion: QuestionDetail = {
        id,
        text,
        keywords,
      };

      this.questionDetails.push(newQuestion);
    });
  }

  private resetFaq(): void {
    this.showQuestion = [];
  }

  private filterFaq(searchTerm: string): void {
    this.showQuestion = [];
    const searchTermSplit: string[] = (searchTerm.match(/[^\s"]+|"([^"]*)"/gi) || [])
        .map((term: string) => term.replace(/"/g, ''))
        .filter((term: string) => term.length > 3);
    for (const questionDetail of this.questionDetails) {
      const id: string = questionDetail.id;
      const fullText: string = questionDetail.text;
      const terms: string[] = questionDetail.keywords;

      if (searchTermSplit.length) {
        let hasMatch: boolean = false;
        for (const term of searchTermSplit) {
          if (term.includes(' ') && fullText.includes(term)) {
            hasMatch = true;
          } else if (!term.includes(' ') && terms.find((keyword: string) => keyword.includes(term))) {
            hasMatch = true;
          }
          if (hasMatch) {
            this.showQuestion.push(id);
            break;
          }
        }
      }
    }
    this.matchFound.emit(this.showQuestion.length);
  }

  /**
   * Checks if any question in the subsection is to be displayed after the search
   */
  displaySubsection(firstPoint: number, lastPoint: number): boolean {
    return !this.key || this.getQuestionsOrder().slice(firstPoint, lastPoint)
        .find((question: string) => this.showQuestion.includes(question)) != null;
  }

  expand(questionId: string): void {
    this.questionsToCollapsed[questionId] = true;
  }

  togglePanel(questionId: string): void {
    this.questionsToCollapsed[questionId] = !this.questionsToCollapsed[questionId];
  }

  /**
   * Scrolls to an HTML element with a given target id.
   */
  jumpTo(target: string): boolean {
    this.pageScrollService.scroll({
      document: this.document,
      scrollTarget: `#${target}`,
      scrollOffset: 70,
    });
    return false;
  }

  abstract getQuestionsOrder(): string[];

}
