import { Injectable } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmationModalType } from '../app/components/confirmation-modal/confirmation-modal-type';
import { ConfirmationModalComponent } from '../app/components/confirmation-modal/confirmation-modal.component';

/**
 * Service to handle display of confirmation modals.
 * For more a complicated use case, use {@link NgbModal} directly.
 */
@Injectable({
  providedIn: 'root',
})
export class ConfirmationModalService {

  constructor(private modalService: NgbModal) {
  }

  /**
   * Opens a confirmation modal
   * @param header to be displayed on the modal
   * @param type which determines the look of the modal
   * @param content to be displayed in the body of the modal. content supports HTML tags
   * @param options See {@code ConfirmationModalOptions}
   */
  open(header: string, type: ConfirmationModalType, content: any, options?: ConfirmationModalOptions): NgbModalRef {
    const modalRef: NgbModalRef = this.modalService.open(ConfirmationModalComponent);
    modalRef.componentInstance.header = header;
    modalRef.componentInstance.content = content;
    modalRef.componentInstance.type = type;
    if (options) {
      Object.entries(options).map(([key, value]: [string, string | boolean]) => {
        modalRef.componentInstance[key] = value;
      });
    }
    return modalRef;
  }
}

/**
 * isNotificationOnly: true will cause modal to only have 1 button
 * confirmMessage: custom text message for confirm button
 * cancelMessage: custom text message for cancel button
 */
export interface ConfirmationModalOptions {
  isNotificationOnly?: boolean;
  confirmMessage?: string;
  cancelMessage?: string;
}
