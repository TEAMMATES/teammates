import { AccountRequestStatus } from 'src/web/types/api-output';

/**
 * Model for the row entries in the account requests table.
 */
export interface AccountRequestTableRowModel {
    id: string;
    name: string;
    email: string;
    status: AccountRequestStatus;
    instituteAndCountry: string;
    createdAtText: string;
    registeredAtText: string;
    comments: string;
    registrationLink: string;
    showLinks: boolean;
}
