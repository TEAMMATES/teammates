import { Component, Input } from '@angular/core';
import { SupportReqEnquiryType, SupportRequestRequest } from 'src/web/types/support-req-types';
import { SupportRequestService } from 'src/web/services/supportrequest.service';
import { finalize } from 'rxjs';
import { StatusMessageService } from 'src/web/services/status-message.service';
import { ErrorMessageOutput } from '../../error-message-output';

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

  constructor(private supportRequestService: SupportRequestService, private statusMessageService: StatusMessageService) {

  }

  resetForm(): void {
    this.model = {
      email: '',
      name: '',
      title: '',
      enquiry_type: SupportReqEnquiryType.GENERAL_HELP,
      initial_msg: '',
    };
  }

  handleSubmitEnquiry(): void {
    this.isFormSubmitting = true;
    // this.model.enquiry_type = (<any>SupportReqEnquiryType)[SupportReqEnquiryType[this.model.enquiry_type.valueOf()]];
    console.log(this.model)
    this.supportRequestService.createSupportRequest({ ...this.model }).pipe(finalize(() => {
      this.isFormSubmitting = false;
    })).subscribe({
      next: () => {
        this.statusMessageService.showSuccessToast('The course has been added.');
        this.resetForm();
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });;
  }
}
