import { Component, EventEmitter, Input, Output } from '@angular/core';

/**
 * Question brief and description edit form component.
 */
@Component({
  selector: 'tm-question-edit-brief-description-form',
  templateUrl: './question-edit-brief-description-form.component.html',
  styleUrls: ['./question-edit-brief-description-form.component.scss'],
})
export class QuestionEditBriefDescriptionFormComponent {

  @Input()
  isDisabled: boolean = false;

  @Input()
  brief: string = '';

  @Input()
  briefPlaceholder: string = '';

  @Input()
  descriptionRichText: string = '';

  @Input()
  descriptionPlaceholderText: string = '';

  @Output()
  briefChange: EventEmitter<string> = new EventEmitter();

  @Output()
  descriptionChange: EventEmitter<string> = new EventEmitter();

  /**
   * Triggers the change of the question brief.
   */
  triggerBriefChange(data: string): void {
    this.briefChange.emit(data);
  }

  /**
   * Triggers the change of the question description.
   */
  triggerDescriptionChange(data: string): void {
    this.descriptionChange.emit(data);
  }

}
