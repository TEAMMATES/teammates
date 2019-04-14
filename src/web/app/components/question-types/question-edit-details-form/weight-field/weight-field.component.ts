import { Component, EventEmitter, Input, Output } from '@angular/core';

/**
 * The input field to specify weights for Mcq/Msq options.
 */
@Component({
  selector: 'tm-weight-field',
  templateUrl: './weight-field.component.html',
  styleUrls: ['./weight-field.component.scss'],
})
export class WeightFieldComponent {

  @Input()
  isEditable: boolean = false;

  @Input()
  weight: number = 1;

  @Output()
  weightEntered: EventEmitter<any> = new EventEmitter();

  constructor() { }

  /**
   * Emit the weight entered to the parent component.
   */
  onWeightEntered(weight: number): void {
    this.weightEntered.emit(weight);
  }
}
