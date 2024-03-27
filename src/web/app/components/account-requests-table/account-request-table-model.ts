/**
 * Model for the row entries in the account requests table.
 */
export interface AccountRequestTableRowModel {
    name: string;
    email: string;
    status: string;
    institute: string;
    country: string;
    createdAtText: string;
    registeredAtText: string;
    comments: string;
    registrationLink: string;
    showLinks: boolean;
}
