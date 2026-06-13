import { NotificationTargetUser, NotificationStyle } from '../../../../types/api-output';

/**
 * The mode of operation for notification edit form.
 */
export enum NotificationEditFormMode {
  /**
   * Adding a new notification.
   */
  ADD,

  /**
   * Editing the existing notification.
   */
  EDIT,
}

/**
 * The form model of notification edit form.
 */
export interface NotificationEditFormModel {
  // EDIT mode specific
  notificationId: string;

  // Undefined until the timestamps are populated
  startTimestamp?: number;
  endTimestamp?: number;

  style: NotificationStyle;
  targetUser: NotificationTargetUser;

  title: string;
  message: string;

  isSaving: boolean;
  isDeleting: boolean;
}
