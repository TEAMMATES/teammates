import { Component, Input } from '@angular/core';
import { StatusMessage } from './status-message';
import { NgFor } from '@angular/common';

/**
 * List of status messages.
 */
@Component({
    selector: 'tm-status-message',
    templateUrl: './status-message.component.html',
    styleUrls: ['./status-message.component.scss'],
    imports: [NgFor],
})
export class StatusMessageComponent {

  @Input() messages: StatusMessage[] = [];

  /**
   * Dismisses the status message.
   */
  dismiss(message: StatusMessage): void {
    this.messages.splice(this.messages.indexOf(message), 1);
  }

}
