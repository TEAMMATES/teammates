import { Pipe, PipeTransform } from '@angular/core';
import { NotificationStyle } from '../../../types/api-output';

/**
 * Maps between notification style and description text.
 */
const descriptionMapping: Record<NotificationStyle, string> = {
  PRIMARY: 'Primary (blue)',
  SECONDARY: 'Secondary (grey)',
  SUCCESS: 'Success (green)',
  DANGER: 'Danger (red)',
  WARNING: 'Warning (yellow)',
  INFO: 'Info (cyan)',
  LIGHT: 'Light',
  DARK: 'Dark',
};

/**
 * Pipe to handle the transformation of an NotificationStyle to a string description.
 */
@Pipe({
  name: 'notificationStyleDescription',
})
export class NotificationStyleDescriptionPipe implements PipeTransform {

  transform(style: NotificationStyle): string {
    return descriptionMapping[style];
  }

}
