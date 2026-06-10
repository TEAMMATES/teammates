import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

/**
 * The input field to specify constsum options to choose from.
 */
@Component({
  selector: 'tm-constsum-options-field',
  templateUrl: './constsum-options-field.component.html',
  styleUrls: ['./constsum-options-field.component.scss'],
  imports: [FormsModule],
})
export class ConstsumOptionsFieldComponent {
  @Input()
  isEditable = false;

  @Input()
  text = '';

  @Output()
  elementDeleted: EventEmitter<void> = new EventEmitter();

  @Output()
  textChange: EventEmitter<string> = new EventEmitter();

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
