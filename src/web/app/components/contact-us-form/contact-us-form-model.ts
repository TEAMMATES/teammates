import { SupportRequestType } from "src/web/types/api-output";

/**
 * The form model of contact us form.
 */
export interface ContactUsFormModel {
  email: string;
  name: string;
  title: string;
  enquiryType: SupportRequestType;
  message: string;
}
