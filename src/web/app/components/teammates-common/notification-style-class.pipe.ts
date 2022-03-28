import { Pipe, PipeTransform } from '@angular/core';
import { NotificationStyle } from '../../../types/api-output';

/**
 * Two available colors for notification font, depending on the style
 */
enum NotificationFontColor {
  WHITE = 'text-white',
  DARK = 'text-dark',
}

/**
 * Maps between notification style and font color.
 */
const fontColorMapping: Record<NotificationStyle, string> = {
  PRIMARY: NotificationFontColor.WHITE,
  SECONDARY: NotificationFontColor.WHITE,
  SUCCESS: NotificationFontColor.WHITE,
  DANGER: NotificationFontColor.WHITE,
  WARNING: NotificationFontColor.DARK,
  INFO: NotificationFontColor.WHITE,
  LIGHT: NotificationFontColor.DARK,
  DARK: NotificationFontColor.WHITE,
};

/**
 * Pipe to handle the transformation of an NotificationStyle to a css class.
 */
@Pipe({
  name: 'notificationStyleClass',
})
export class NotificationStyleClassPipe implements PipeTransform {

  transform(style: NotificationStyle): string {
    return `bg-${style.toLowerCase()} ${fontColorMapping[style]}`;
  }

}
