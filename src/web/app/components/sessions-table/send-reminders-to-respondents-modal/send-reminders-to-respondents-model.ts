import {
  InstructorListInfoTableRowModel,
  StudentListInfoTableRowModel,
} from '../respondent-list-info-table/respondent-list-info-table-model';

/**
 * The model for a reminder response.
 */
export interface ReminderResponseModel {
  respondentsToSend: (StudentListInfoTableRowModel | InstructorListInfoTableRowModel)[];
  isSendingCopyToInstructor: boolean;
}
