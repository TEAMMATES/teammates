import { AccountVerificationRequestStatus } from '../../../types/api-output';

/**
 * Model for the row entries in the account verification requests table.
 */
export interface AccountVerificationRequestTableRowModel {
  id: string;
  name: string;
  email: string;
  status: AccountVerificationRequestStatus;
  institute: string;
  country: string;
  createdAtText: string;
  createdDemoCourseAtText: string;
  comments: string;
  registrationLink: string;
  showLinks: boolean;
}
