import { NotificationTargetUser, NotificationStyle } from '../../../../types/api-request';
import { DateFormat, TimeFormat } from '../../../../types/datetime-const';

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
  shown: boolean;

  startTime: TimeFormat;
  startDate: DateFormat;
  endTime: TimeFormat;
  endDate: DateFormat;

  style: NotificationStyle;
  targetUser: NotificationTargetUser;

  title: string;
  message: string;

  isSaving: boolean;
  isDeleting: boolean;
}
