import { SupportRequestType } from "./api-output"

export interface SupportRequest extends SupportRequestRequest {
    trackingId: string,
    status: SupportReqStatus
}

export interface SupportRequestRequest {
    email: string,
    name: string,
    type: SupportRequestType,
    title: string,
    message: string,
    createdAt: number,
    updatedAt: number,
}

/**
 * The ordering in the supportRequestList is based on the order of this list
 */
export enum SupportReqStatus {
    NEW,
    AWAITING_ADMIN,
    AWAITING_USER,
    RESOLVED
}
