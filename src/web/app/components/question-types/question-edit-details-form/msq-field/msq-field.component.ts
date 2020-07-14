import { Component, EventEmitter, Input, Output } from '@angular/core';
import { StatusMessageService } from '../../../../../services/status-message.service';

/**
 * The input field to specify options to choose from.
 */
@Component({
  selector: 'tm-msq-field',
  templateUrl: './msq-field.component.html',
  styleUrls: ['./msq-field.component.scss'],
})
export class MsqFieldComponent {

  @Input()
  isEditable: boolean = false;

  @Input()
  numberOfMsqChoices: number = 1;

  @Input()
  text: string = '';

  @Input()
  index: number = 0;

  @Output()
  elementDeleted: EventEmitter<any> = new EventEmitter();

  @Output()
  msqText: EventEmitter<any> = new EventEmitter();

  constructor(private statusMessageService: StatusMessageService) { }

  /**
   * Deletes a Msq option.
   */
  deleteMsqOption(): void {
    if (this.numberOfMsqChoices > 2) {
      this.elementDeleted.emit(this.index);
    } else {
      this.statusMessageService.showErrorToast('There must be at least two Msq options.');
    }
  }

  /**
   * When user enters an Msq option text, emit the change to parent component.
   */
  onMsqOptionEntered(text: string): void {
    this.msqText.emit(text);
  }

}
