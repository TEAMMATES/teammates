import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FeedbackQuestionType } from '../../../types/api-output';
import { NgbDropdown, NgbDropdownToggle, NgbDropdownMenu } from '@ng-bootstrap/ng-bootstrap';
import { NgFor, NgIf } from '@angular/common';
import { TeammatesRouterDirective } from '../teammates-router/teammates-router.directive';
import { AjaxLoadingComponent } from '../ajax-loading/ajax-loading.component';
import { EnumToArrayPipe } from '../teammates-common/enum-to-array.pipe';
import { QuestionTypeNamePipe } from '../teammates-common/question-type-name.pipe';
import { QuestionTypeHelpPathPipe } from '../teammates-common/question-type-help-path.pipe';

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
    NgFor,
    NgIf,
    TeammatesRouterDirective,
    AjaxLoadingComponent,
    EnumToArrayPipe,
    QuestionTypeNamePipe,
    QuestionTypeHelpPathPipe,
  ],
})
export class AddingQuestionPanelComponent {

  // enum
  FeedbackQuestionType: typeof FeedbackQuestionType = FeedbackQuestionType;

  @Input()
  isCopyingQuestion: boolean = false;

  @Input()
  isLinkDisabled: boolean = false;

  @Output()
  templateQuestionModalEvent: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  populateAndShowNewQuestionFormEvent: EventEmitter<FeedbackQuestionType> = new EventEmitter<FeedbackQuestionType>();

  @Output()
  copyQuestionsFromOtherSessionsEvent: EventEmitter<void> = new EventEmitter<void>();

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
