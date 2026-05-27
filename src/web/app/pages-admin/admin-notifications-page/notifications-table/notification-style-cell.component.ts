import { NgClass } from '@angular/common';
import { Component, Input } from '@angular/core';
import { NotificationStyle } from '../../../../types/api-output';
import { NotificationStyleClassPipe } from '../../../components/teammates-common/notification-style-class.pipe';
import { NotificationStyleDescriptionPipe } from '../../../components/teammates-common/notification-style-description.pipe';

@Component({
  selector: 'tm-notification-style-cell',
  templateUrl: './notification-style-cell.component.html',
  imports: [NgClass, NotificationStyleClassPipe, NotificationStyleDescriptionPipe],
})
export class NotificationStyleCellComponent {
  @Input() style: NotificationStyle = NotificationStyle.SUCCESS;
}
