import { SupportReqEnquiryType } from "src/web/types/support-req-types";

/**
 * The form model of contact us form.
 */
export interface ContactUsFormModel {
  email: string;
  name: string;
  title: string;
  enquiryType: SupportReqEnquiryType;
  message: string;
}
