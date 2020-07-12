import { Component, EventEmitter, Input, Output } from '@angular/core';
import { StatusMessageService } from '../../../../../services/status-message.service';

/**
 * The input field to specify options to choose from.
 */
@Component({
  selector: 'tm-rank-options-field',
  templateUrl: './rank-options-field.component.html',
  styleUrls: ['./rank-options-field.component.scss'],
})
export class RankOptionsFieldComponent {

  @Input()
  isEditable: boolean = false;

  @Input()
  numberOfRankChoices: number = 1;

  @Input()
  text: string = '';

  @Input()
  index: number = 0;

  @Output()
  elementDeleted: EventEmitter<any> = new EventEmitter();

  @Output()
  rankOptionText: EventEmitter<any> = new EventEmitter();

  constructor(private statusMessageService: StatusMessageService) { }

  /**
   * When user enters an Rank option text, emit the change to parent component.
   */
  onRankOptionEntered(text: string): void {
    this.rankOptionText.emit(text);
  }

  /**
   * Deletes a Rank option.
   */
  deleteRankOption(): void {
    if (this.numberOfRankChoices > 2) {
      this.elementDeleted.emit(this.index);
    } else {
      this.statusMessageService.showErrorToast('There must be at least two Rank options.');
    }
  }

}
