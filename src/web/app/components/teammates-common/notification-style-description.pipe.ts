import { Pipe, PipeTransform } from '@angular/core';
import { NotificationStyle } from '../../../types/api-output';
import { notificationStyleConfigMap } from '../../../types/notification-style-config';

/**
 * Pipe to handle the transformation of an NotificationStyle to a string description.
 */
@Pipe({
  name: 'notificationStyleDescription',
})
export class NotificationStyleDescriptionPipe implements PipeTransform {

  transform(style: NotificationStyle): string {
    return notificationStyleConfigMap[style].description;
  }

}
