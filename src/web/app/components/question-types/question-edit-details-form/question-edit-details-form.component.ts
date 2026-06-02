import { Directive, EventEmitter, Input, Output } from '@angular/core';
import { FeedbackQuestionDetails } from '../../../../types/api-output';

/**
 * The abstract question details edit form component.
 */
@Directive()
export abstract class QuestionEditDetailsFormComponent<D extends FeedbackQuestionDetails> {
  model!: D;

  @Input()
  isEditable = true;

  @Input()
  set details(details: FeedbackQuestionDetails) {
    this.model = details as D;
  }

  @Output()
  detailsChange: EventEmitter<FeedbackQuestionDetails> = new EventEmitter();

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(field: keyof D, data: D[keyof D]): void {
    this.detailsChange.emit({ ...this.model, [field]: data });
  }

  /**
   * Triggers changes of the question details for the form.
   */
  triggerModelChangeBatch(obj: Partial<D>): void {
    this.detailsChange.emit({ ...this.model, ...obj });
  }

  onIntegerInput(event: KeyboardEvent): void {
    const { key } = event;
    const isBackspace = key === 'Backspace';
    const isDigit = /[0-9]/.test(key);
    if (!isBackspace && !isDigit) {
      event.preventDefault();
    }
  }

  onFloatInput(event: KeyboardEvent): void {
    const { key } = event;
    const isBackspace = key === 'Backspace';
    const isDecimal = key === '.';
    const isDigit = /[0-9]/.test(key);
    if (!isBackspace && !isDigit && !isDecimal) {
      event.preventDefault();
    }
  }

  onPaste(event: ClipboardEvent): void {
    const { clipboardData } = event;
    if (clipboardData == null) {
      return;
    }
    const pastedText = clipboardData.getData('text');
    const isDigit = /^\d+$/.test(pastedText);
    if (!isDigit) {
      event.preventDefault();
    }
  }

  restrictIntegerInputLength(input: HTMLInputElement, field: keyof D): void {
    if (input.value != null && input.value.length > 9) {
      input.value = input.value.substring(0, 9);
      this.triggerModelChange(field, parseInt(input.value, 10) as any);
    }
  }

  restrictFloatInputLength(input: HTMLInputElement, field: keyof D): void {
    if (input.value != null && input.value.length > 9) {
      input.value = input.value.substring(0, 9);
      this.triggerModelChange(field, parseFloat(input.value) as any);
    }
  }
}
