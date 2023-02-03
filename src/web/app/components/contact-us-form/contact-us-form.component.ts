import { Component, Input } from '@angular/core';
import { ContactUsFormModel } from './contact-us-form-model';
import { SupportReqEnquiryType } from 'src/web/types/support-req-types';

@Component({
  selector: 'tm-contact-us-form',
  templateUrl: './contact-us-form.component.html',
  styleUrls: ['./contact-us-form.component.scss'],
})
export class ContactUsFormComponent {

  // enum
  SupportReqEnquiryType: typeof SupportReqEnquiryType = SupportReqEnquiryType;

  readonly ENQUIRY_TYPES: SupportReqEnquiryType[] = [
    SupportReqEnquiryType.GENERAL_HELP,
    SupportReqEnquiryType.NEW_ACCOUNT,
  ]

  @Input()
  model: ContactUsFormModel = {
    email: '',
    name: '',
    title: '',
    enquiryType: SupportReqEnquiryType.GENERAL_HELP,
    message: '',
  };

  isFormSubmitting: boolean = false;

  handleSubmitEnquiry(): void {
    this.isFormSubmitting = true;
    console.log(this.model);
    this.isFormSubmitting = false;
  }

}
