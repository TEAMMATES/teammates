import { Component, EventEmitter, Input, Output } from '@angular/core';
import { McqQuestionEditDetailsFormComponent } from '../mcq-question-edit-details-form.component';

/**
 * The input field to specify options to choose form.
 */
@Component({
  selector: 'tm-mcq-field',
  templateUrl: './mcq-field.component.html',
  styleUrls: ['./mcq-field.component.scss'],
})
export class McqFieldComponent extends McqQuestionEditDetailsFormComponent {

  @Input()
  isEditable: boolean = false;

  @Input()
  index: number = 0;

  @Input()
  numberOfMcqChoices: number = 1;

  @Input()
  text: string = '';

  @Output()
  elementDeleted: EventEmitter<any> = new EventEmitter();

  @Output()
  mcqText: EventEmitter<any> = new EventEmitter();

  constructor() {
    super();
  }

  /**
   * Deletes a Mcq.
   */
  deleteMcq(): void {
    if (this.numberOfMcqChoices > 1) {
      this.elementDeleted.emit();
    }
  }

  /**
   * When user enters an mcq, emit the change to parent component.
   */
  onSearchChange(text: string): void {
    this.mcqText.emit(text);
  }

}
