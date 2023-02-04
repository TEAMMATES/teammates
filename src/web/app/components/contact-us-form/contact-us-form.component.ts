import { Component, Input } from '@angular/core';
import { SupportReqEnquiryType, SupportRequestRequest } from 'src/web/types/support-req-types';
import { SupportRequestService } from 'src/web/services/supportrequest.service';

@Component({
  selector: 'tm-contact-us-form',
  templateUrl: './contact-us-form.component.html',
  styleUrls: ['./contact-us-form.component.scss'],
})
export class ContactUsFormComponent {

  // enum
  SupportReqEnquiryType: typeof SupportReqEnquiryType = SupportReqEnquiryType;

  ENQUIRY_TYPES = Object.keys(SupportReqEnquiryType).slice(0, Object.keys(SupportReqEnquiryType).length / 2)

  @Input()
  model: SupportRequestRequest = {
    email: '', 
    name: '',
    title: '',
    enquiry_type: SupportReqEnquiryType.GENERAL_HELP,
    initial_msg: '',
  };

  isFormSubmitting: boolean = false;

  constructor(private supportRequestService: SupportRequestService) {

  }

  createNewSupportRequest(req: SupportRequestRequest) {
    this.supportRequestService.createSupportRequest(req)
  }

  handleSubmitEnquiry(): void {
    this.isFormSubmitting = true;
    // this.model.enquiry_type = (<any>SupportReqEnquiryType)[SupportReqEnquiryType[this.model.enquiry_type.valueOf()]];
    // console.log(this.model)
    this.createNewSupportRequest({...this.model})
    this.isFormSubmitting = false;
  }
}
