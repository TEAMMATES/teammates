import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgbDropdown, NgbDropdownToggle, NgbDropdownMenu } from '@ng-bootstrap/ng-bootstrap/dropdown';
import { FeedbackQuestionType } from '../../../types/api-output';
import { AjaxLoadingComponent } from '../ajax-loading/ajax-loading.component';
import { QuestionTypeHelpPathPipe } from '../teammates-common/question-type-help-path.pipe';
import { QuestionTypeNamePipe } from '../teammates-common/question-type-name.pipe';
import { RouterLink } from '@angular/router';

/**
 * Displaying the adding questions panel.
 */
@Component({
  selector: 'tm-adding-question-panel',
  templateUrl: './adding-question-panel.component.html',
  styleUrls: ['./adding-question-panel.component.scss'],
  imports: [
    NgbDropdown,
    NgbDropdownToggle,
    NgbDropdownMenu,
    RouterLink,
    AjaxLoadingComponent,
    QuestionTypeNamePipe,
    QuestionTypeHelpPathPipe,
  ],
})
export class AddingQuestionPanelComponent {
  // enum
  FeedbackQuestionType!: typeof FeedbackQuestionType;

  readonly questionTypes = Object.values(FeedbackQuestionType);

  @Input()
  isCopyingQuestion = false;

  @Input()
  isLinkDisabled = false;

  @Output()
  templateQuestionModalEvent: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  populateAndShowNewQuestionFormEvent: EventEmitter<FeedbackQuestionType> = new EventEmitter<FeedbackQuestionType>();

  @Output()
  copyQuestionsFromOtherSessionsEvent: EventEmitter<void> = new EventEmitter<void>();

  constructor() {
    this.FeedbackQuestionType = FeedbackQuestionType;
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
   * Handles 'Copy Question' click event.
   */
  copyQuestionsFromOtherSessionsHandler(): void {
    this.copyQuestionsFromOtherSessionsEvent.emit();
  }
}
