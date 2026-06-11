import { Component, Input } from '@angular/core';
import { NotificationStyle } from '../../../../types/api-output';
import { NotificationStyleDescriptionPipe } from '../../../components/teammates-common/notification-style-description.pipe';

@Component({
  selector: 'tm-notification-style-cell',
  templateUrl: './notification-style-cell.component.html',
  imports: [NotificationStyleDescriptionPipe],
})
export class NotificationStyleCellComponent {
  @Input() style: NotificationStyle = NotificationStyle.SUCCESS;
}
