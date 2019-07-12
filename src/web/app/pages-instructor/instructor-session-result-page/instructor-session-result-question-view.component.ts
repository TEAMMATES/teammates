import { Component, EventEmitter, OnChanges, OnInit, Output } from '@angular/core';
import { InstructorSessionResultView } from './instructor-session-result-view';
import { InstructorSessionResultViewType } from './instructor-session-result-view-type.enum';

/**
 * Instructor sessions results page question view.
 */
@Component({
  selector: 'tm-instructor-session-result-question-view',
  templateUrl: './instructor-session-result-question-view.component.html',
  styleUrls: ['./instructor-session-result-question-view.component.scss'],
})
export class InstructorSessionResultQuestionViewComponent
    extends InstructorSessionResultView implements OnInit, OnChanges {

  @Output()
  loadQuestion: EventEmitter<string> = new EventEmitter();

  questionsOrder: any[] = [];

  constructor() {
    super(InstructorSessionResultViewType.QUESTION);
  }

  ngOnChanges(): void {
    if (this.questionsOrder.length > 0) {
      for (const questionId of Object.keys(this.responses)) {
        const response: any = this.responses[questionId];
        this.questionsOrder[response.questionNumber - 1].responses = Object.assign([], response.responses);
      }
    }
  }

  ngOnInit(): void {
    for (const questionId of Object.keys(this.responses)) {
      const response: any = this.responses[questionId];
      this.questionsOrder[response.questionNumber] = response;
    }
    this.questionsOrder = this.questionsOrder.filter((questionId: string) => questionId);
  }

  /**
   * Expands the tab of the specified question.
   */
  expandQuestionTab(question: any): void {
    question.isTabExpanded = !question.isTabExpanded;
    if (question.isTabExpanded) {
      this.loadQuestion.emit(question.feedbackQuestionId);
    }
  }

}
