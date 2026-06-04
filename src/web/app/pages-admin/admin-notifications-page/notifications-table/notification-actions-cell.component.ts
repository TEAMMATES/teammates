import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'tm-notification-actions-cell',
  templateUrl: './notification-actions-cell.component.html',
})
export class NotificationActionsCellComponent {
  @Output() editClicked: EventEmitter<void> = new EventEmitter();
  @Output() deleteClicked: EventEmitter<void> = new EventEmitter();
}
