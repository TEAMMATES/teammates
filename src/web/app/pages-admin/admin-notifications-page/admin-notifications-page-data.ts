import { Notification, NotificationStyle, NotificationTargetUser } from '../../../types/api-output';
import { NotificationEditFormModel } from './notification-edit-form/notification-edit-form-model';

/**
 * Structure for example of notification edit model.
 */
export const EXAMPLE_NOTIFICATION_EDIT_MODEL: NotificationEditFormModel = {
  notificationId: 'notification1',
  shown: false,

  startTime: { hour: 0, minute: 0 },
  startDate: { year: 0, month: 0, day: 0 },
  endTime: { hour: 0, minute: 0 },
  endDate: { year: 0, month: 0, day: 0 },

  style: NotificationStyle.SUCCESS,
  targetUser: NotificationTargetUser.INSTRUCTOR,

  title: 'valid title',
  message: 'valid message',

  isSaving: false,
  isDeleting: false,
};
/**
 * Structure for example of notification.
 */
export const EXAMPLE_NOTIFICATION_ONE: Notification = {
  notificationId: 'notification1',
  startTimestamp: 0,
  endTimestamp: 1,
  createdAt: 0,
  updatedAt: 0,
  style: NotificationStyle.SUCCESS,
  targetUser: NotificationTargetUser.INSTRUCTOR,
  title: 'valid title',
  message: 'valid message',
  shown: false,
};
/**
 * Structure for example of notification.
 */
export const EXAMPLE_NOTIFICATION_TWO: Notification = {
  notificationId: 'notification2',
  startTimestamp: 1554960204,
  endTimestamp: 1554977204,
  createdAt: 1554232400,
  updatedAt: 1554232400,
  style: NotificationStyle.DANGER,
  targetUser: NotificationTargetUser.GENERAL,
  title: 'valid title 2',
  message: 'valid message 2',
  shown: false,
};
