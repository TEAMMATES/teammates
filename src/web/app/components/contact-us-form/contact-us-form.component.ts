import { Component, Input } from '@angular/core';
import { SupportRequestRequest } from 'src/web/types/support-req-types';
import { SupportRequestType } from 'src/web/types/api-output';
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
  SupportRequestType: typeof SupportRequestType = SupportRequestType;

  ENQUIRY_TYPES = [
    SupportRequestType.GENERAL_ENQUIRY,
    SupportRequestType.NEW_ACCT
  ];

  @Input()
  model: SupportRequestRequest = {
    email: '',
    name: '',
    title: '',
    type: SupportRequestType.GENERAL_ENQUIRY,
    message: '',
    createdAt: new Date().valueOf(),
    updatedAt: new Date().valueOf(),
  };

  isFormSubmitting: boolean = false;

  constructor(private supportRequestService: SupportRequestService, private statusMessageService: StatusMessageService) {

  }

  resetForm(): void {
    this.model = {
      email: '',
      name: '',
      title: '',
      type: SupportRequestType.GENERAL_ENQUIRY,
      message: '',
      createdAt: new Date().valueOf(),
      updatedAt: new Date().valueOf(),
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
        this.statusMessageService.showSuccessToast('The support request has been submitted.');
        this.resetForm();
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });;
  }
}
