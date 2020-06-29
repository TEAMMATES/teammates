import { Component, EventEmitter, Input, Output } from '@angular/core';
import { StatusMessageService } from '../../../../../services/status-message.service';

/**
 * The input field to specify options to choose from.
 */
@Component({
  selector: 'tm-mcq-field',
  templateUrl: './mcq-field.component.html',
  styleUrls: ['./mcq-field.component.scss'],
})
export class McqFieldComponent {

  @Input()
  isEditable: boolean = false;

  @Input()
  numberOfMcqChoices: number = 1;

  @Input()
  text: string = '';

  @Input()
  index: number = 0;

  @Output()
  elementDeleted: EventEmitter<any> = new EventEmitter();

  @Output()
  mcqText: EventEmitter<any> = new EventEmitter();

  constructor(private statusMessageService: StatusMessageService) {}

  /**
   * Deletes a Mcq option.
   */
  deleteMcqOption(): void {
    if (this.numberOfMcqChoices > 2) {
      this.elementDeleted.emit(this.index);
    } else {
      this.statusMessageService.showErrorToast('There must be at least two Mcq options.');
    }
  }

  /**
   * When user enters an Mcq option text, emit the change to parent component.
   */
  onMcqOptionEntered(text: string): void {
    this.mcqText.emit(text);
  }

}
