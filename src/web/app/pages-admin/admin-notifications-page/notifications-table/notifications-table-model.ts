// FIXME: This Notification is to be imported from api-output after GET route PR is merged
import { Notification } from '../../../../services/notification.service';

/**
 * The model for a row in the notifications table.
 */
export interface NotificationsTableRowModel {
  index: number,
  notification: Notification,
}

/**
 * The color scheme of the header of the table
 */
export enum NotificationsTableHeaderColorScheme {
  /**
   * Blue background with white text.
   */
  BLUE,

  /**
   * White background with black text.
   */
  WHITE,
}
