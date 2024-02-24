import { Directive, EventEmitter, Input, Output } from '@angular/core';
import { FeedbackQuestionDetails } from '../../../../types/api-output';

/**
 * The abstract question details edit form component.
 */
@Directive()
// eslint-disable-next-line @angular-eslint/directive-class-suffix
export abstract class QuestionEditDetailsFormComponent<D extends FeedbackQuestionDetails> {

  model: D;

  @Input()
  isEditable: boolean = true;

  @Input()
  set details(details: FeedbackQuestionDetails) {
    this.model = details as D;
  }

  @Output()
  detailsChange: EventEmitter<FeedbackQuestionDetails> = new EventEmitter();

  protected constructor(model: D) {
    this.model = model;
  }

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

  restrictIntegerInputLength(event : InputEvent, field: keyof D) : void {
    const target : HTMLInputElement = event.target as HTMLInputElement;
    if (target.value != null && target.value.length > 9) {
      target.value = target.value.substring(0, 9);
      this.triggerModelChange(field, parseInt(target.value, 10) as any);
    }
  }

  restrictFloatInputLength(event : InputEvent, field: keyof D) : void {
    const target : HTMLInputElement = event.target as HTMLInputElement;
    if (target.value != null && target.value.length > 9) {
      target.value = target.value.substring(0, 9);
      this.triggerModelChange(field, parseFloat(target.value) as any);
    }
  }

}
