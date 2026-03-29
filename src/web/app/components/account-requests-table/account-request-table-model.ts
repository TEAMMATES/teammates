import { AccountRequestStatus } from '../../../types/api-output';

/**
 * Model for the row entries in the account requests table.
 */
export interface AccountRequestTableRowModel {
    id: string;
    localId?: string;
    name: string;
    email: string;
    status: AccountRequestStatus | 'DRAFT' | 'ADDING' | 'FAIL';
    instituteAndCountry: string;
    createdAtText: string;
    registeredAtText: string;
    comments: string;
    registrationLink: string;
    showLinks: boolean;
    isLocalRow?: boolean;
}
