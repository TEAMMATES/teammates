import { EventEmitter, Component, Input, OnInit, Output } from '@angular/core';
import { FeedbackQuestionType } from '../../../types/api-output';

/**
 * Panel for adding questions
 */
@Component({
  selector: 'tm-adding-question-panel',
  templateUrl: './adding-question-panel.component.html',
  styleUrls: ['./adding-question-panel.component.scss']
})
export class AddingQuestionPanelComponent implements OnInit {

  //enum
  FeedbackQuestionType: typeof FeedbackQuestionType = FeedbackQuestionType;

  @Input()
  isCopyingQuestion: boolean = false;

  @Input()
  FeedbackQuestionTypes: FeedbackQuestionType = FeedbackQuestionType.TEXT;

  @Output()
  templateQuestionModalEvent: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  populateAndShowNewQuestionFormEvent: EventEmitter<FeedbackQuestionType> = new EventEmitter<FeedbackQuestionType>();

  @Output()
  copyQuestionsFromOtherSessionsEvent: EventEmitter<void> = new EventEmitter<void>();

  constructor() {
  }

  ngOnInit(): void {
  }

  /**
   * Handles display of template question modal.
   */
  templateQuestionModalHandler(): void {
    this.templateQuestionModalEvent.emit();
  }

  /**
   * Populates and shows new question edit form.
   */
  populateAndShowNewQuestionFormHandler(type: FeedbackQuestionType): void {
    this.populateAndShowNewQuestionFormEvent.emit(type);
  }

  /**
   * Redirects info button
   */
   //infoButtonHandler(): void {
      // TODO
   //}

  /**
   * Handles 'Copy Question' click event.
   */
   copyQuestionsFromOtherSessionsHandler(): void {
    this.copyQuestionsFromOtherSessionsEvent.emit();
   }


}
