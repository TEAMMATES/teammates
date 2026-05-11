import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

/**
 * The input field to specify weights for Mcq/Msq options.
 */
@Component({
  selector: 'tm-weight-field',
  templateUrl: './weight-field.component.html',
  imports: [FormsModule],
})
export class WeightFieldComponent {
  @Input()
  isEditable = false;

  @Input()
  weight = 1;

  @Output()
  weightEntered: EventEmitter<any> = new EventEmitter();

  /**
   * Emit the weight entered to the parent component.
   */
  onWeightEntered(weight: number): void {
    this.weightEntered.emit(weight);
  }
}
