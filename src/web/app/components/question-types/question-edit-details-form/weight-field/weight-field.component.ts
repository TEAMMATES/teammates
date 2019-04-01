import { Component, EventEmitter, Input, Output } from '@angular/core';

/**
 * The input field to specify weights for Mcq options.
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
  mcqWeight: EventEmitter<any> = new EventEmitter();

  constructor() { }

  /**
   * Emit the weight entered to the parent component.
   */
  onMcqWeightEntered(weight: number): void {
    this.mcqWeight.emit(weight);
  }
}
