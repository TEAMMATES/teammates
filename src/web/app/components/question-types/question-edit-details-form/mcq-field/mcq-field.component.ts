import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { StatusMessageService } from '../../../../../services/status-message.service';

/**
 * The input field to specify options to choose from.
 */
@Component({
  selector: 'tm-mcq-field',
  templateUrl: './mcq-field.component.html',
  styleUrls: ['./mcq-field.component.scss'],
  imports: [FormsModule],
})
export class McqFieldComponent {
  private statusMessageService = inject(StatusMessageService);

  @Input()
  isEditable = false;

  @Input()
  numberOfMcqChoices = 1;

  @Input()
  text = '';

  @Input()
  index = 0;

  @Input()
  isQuestionDropdownEnabled = false;

  @Output()
  elementDeleted: EventEmitter<any> = new EventEmitter();

  @Output()
  mcqText: EventEmitter<any> = new EventEmitter();

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
