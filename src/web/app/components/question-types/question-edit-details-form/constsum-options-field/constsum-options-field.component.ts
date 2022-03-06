import { Component, EventEmitter, Input, Output } from '@angular/core';

/**
 * The input field to specify constsum options to choose from.
 */
@Component({
  selector: 'tm-constsum-options-field',
  templateUrl: './constsum-options-field.component.html',
  styleUrls: ['./constsum-options-field.component.scss'],
})
export class ConstsumOptionsFieldComponent {

  @Input()
  isEditable: boolean = false;

  @Input()
  text: string = '';

  @Output()
  elementDeleted: EventEmitter<any> = new EventEmitter();

  @Output()
  textChange: EventEmitter<any> = new EventEmitter();

  /**
   * When user enters an option text, emit the change to parent component.
   */
  onConstsumOptionEntered(text: string): void {
    this.textChange.emit(text);
  }

  /**
   * Deletes an option.
   */
  deleteConstsumOption(): void {
    this.elementDeleted.emit();
  }

}
