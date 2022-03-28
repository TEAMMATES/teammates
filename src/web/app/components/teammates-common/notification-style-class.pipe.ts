import { Pipe, PipeTransform } from '@angular/core';
import { NotificationStyle } from '../../../types/api-output';
import { notificationStyleConfigMap } from '../../../types/notification-style-config';

/**
 * Pipe to handle the transformation of an NotificationStyle to a css class.
 */
@Pipe({
  name: 'notificationStyleClass',
})
export class NotificationStyleClassPipe implements PipeTransform {

  transform(style: NotificationStyle): string {
    return `bg-${style.toLowerCase()} ${notificationStyleConfigMap[style].font}`;
  }

}
