import { AccountRequestStatus } from '../../../types/api-output';

/**
 * Model for the row entries in the account requests table.
 */
export interface AccountRequestTableRowModel {
  id: string;
  name: string;
  email: string;
  status: AccountRequestStatus;
  institute: string;
  country: string;
  createdAtText: string;
  createdDemoCourseAtText: string;
  comments: string;
  registrationLink: string;
  showLinks: boolean;
}
