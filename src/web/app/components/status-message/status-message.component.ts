import { Component, Input } from '@angular/core';
import { StatusMessage } from './status-message';

/**
 * List of status messages.
 */
@Component({
  selector: 'tm-status-message',
  templateUrl: './status-message.component.html',
  styleUrls: ['./status-message.component.scss'],
})
export class StatusMessageComponent {

  @Input() messages: StatusMessage[] = [];

  constructor() { }

  /**
   * Dismisses the status message.
   */
  dismiss(message: StatusMessage): void {
    this.messages.splice(this.messages.indexOf(message), 1);
  }

}
