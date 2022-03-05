/**
 * The format of a session template.
 */
import { DateFormat } from '../../../components/datepicker/datepicker.component';
import { TimeFormat } from '../../../components/timepicker/timepicker.component';

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
 * The form model of session edit form.
 */
export interface NotificationEditFormModel {
  // EDIT mode specific
  notificationId: string;
  shown: boolean;

  startTime: TimeFormat;
  startDate: DateFormat;
  endTime: TimeFormat;
  endDate: DateFormat;

  type: string;
  targetUser: string;

  title: string;
  message: string;

  isSaving: boolean;
  isEditable: boolean;
  isDeleting: boolean;
}
