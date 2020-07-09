import { Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';

import { collapseAnim } from '../../components/teammates-common/collapse-anim';
import { QuestionTabModel } from './instructor-session-result-page.component';
import { InstructorSessionResultView } from './instructor-session-result-view';
import { InstructorSessionResultViewType } from './instructor-session-result-view-type.enum';

/**
 * Instructor sessions results page question view.
 */
@Component({
  selector: 'tm-instructor-session-result-question-view',
  templateUrl: './instructor-session-result-question-view.component.html',
  styleUrls: ['./instructor-session-result-question-view.component.scss'],
  animations: [collapseAnim],
})
export class InstructorSessionResultQuestionViewComponent
    extends InstructorSessionResultView implements OnInit, OnChanges {

  @Output()
  loadQuestion: EventEmitter<string> = new EventEmitter();

  @Input() questions: Record<string, QuestionTabModel> = {};
  @Input() isDisplayOnly: boolean = false;

  @Output() downloadQuestionResult: EventEmitter<{
    questionNumber: number,
    questionId: string,
  }> = new EventEmitter();

  questionsOrder: QuestionTabModel[] = [];

  constructor() {
    super(InstructorSessionResultViewType.QUESTION);
  }

  ngOnInit(): void {
    this.sortQuestion();
  }

  ngOnChanges(): void {
    this.sortQuestion();
  }

  sortQuestion(): void {
    this.questionsOrder = Object.values(this.questions)
        .sort((val1: QuestionTabModel, val2: QuestionTabModel) => {
          return val1.question.questionNumber - (val2.question.questionNumber);
        });
  }

  /**
   * Triggers the download of a question result.
   */
  triggerDownloadQuestionResult($event: { questionNumber: number, questionId: string }): void {
    this.downloadQuestionResult.emit($event);
  }
}
