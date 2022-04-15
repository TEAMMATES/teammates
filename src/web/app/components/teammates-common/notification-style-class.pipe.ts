import { Pipe, PipeTransform } from '@angular/core';
import { NotificationStyle } from '../../../types/api-output';

/**
 * Pipe to handle the transformation of an NotificationStyle to a css class.
 */
@Pipe({
  name: 'notificationStyleClass',
})
export class NotificationStyleClassPipe implements PipeTransform {

  transform(style: NotificationStyle): string {
    return `alert alert-${style.toLowerCase()}`;
  }

}
