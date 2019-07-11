import { EventEmitter, Input, OnInit, Output } from '@angular/core';
import { InstructorSessionResultSectionType } from './instructor-session-result-section-type.enum';
import { InstructorSessionResultViewType } from './instructor-session-result-view-type.enum';

/**
 * Abstract component for all different view type components of instructor sessions result page.
 */
export abstract class InstructorSessionResultView implements OnInit {

  @Input() responses: { [key: string]: any } = {};
  @Input() section: string = '';
  @Input() sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;
  @Input() groupByTeam: boolean = true;
  @Input() showStatistics: boolean = true;
  @Input() indicateMissingResponses: boolean = true;

  @Output() commentsChangeInResponse: EventEmitter<any> = new EventEmitter();

  constructor(protected viewType: InstructorSessionResultViewType) {}

  ngOnInit(): void {
  }

  /**
   * Triggers the event concerning change response comments in a question
   */
  triggerCommentsChangeInQuestionEvent(questionId: string, responses: any): void {
    const questionsModel: any = {...this.responses};
    questionsModel[questionId].responses = responses;
    this.commentsChangeInResponse.emit(questionsModel);
  }
}
