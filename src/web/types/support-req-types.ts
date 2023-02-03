export interface SupportRequest { 
    trackingId: string, 
    email: string, 
    name: string, 
    enquiry_type: SupportReqEnquiryType, 
    title: string, 
    initial_msg: string, 
    status: SupportReqStatus
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

/**
 * The ordering in the supportRequestList is based on the order of this list
 */
export enum SupportReqEnquiryType { 
    NEW_ACCOUNT, 
    GENERAL_HELP
}