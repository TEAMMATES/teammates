import { Notification } from '../../../../types/api-output';

/**
 * The model for a row in the notifications table.
 */
export interface NotificationsTableRowModel {
  isHighlighted: boolean;
  notification: Notification;
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
