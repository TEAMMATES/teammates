import { Injectable, TemplateRef } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { SimpleModalType } from '../app/components/simple-modal/simple-modal-type';
import { SimpleModalComponent } from '../app/components/simple-modal/simple-modal.component';

/**
 * Optional parameters for modal.
 */
export interface SimpleModalOptions {
  // determines if there should be 2 buttons for confirmation or only 1 button to close the modal
  isInformationOnly?: boolean;
  confirmMessage?: string; // custom text message for confirm button
  cancelMessage?: string; // custom text message for cancel button
}

/**
 * Service to handle display of confirmation modals.
 * For more a complicated use case, use {@link NgbModal} directly.
 */
@Injectable({
  providedIn: 'root',
})
export class SimpleModalService {

  constructor(private ngbModal: NgbModal) {
  }

  /**
   * Opens a confirmation modal
   * @param header to be displayed on the modal
   * @param type which determines the look of the modal
   * @param content to be displayed in the body of the modal. content supports HTML tags
   * @param options See {@code SimpleModalOptions}
   */
  private open(header: string, type: SimpleModalType,
       content: string | TemplateRef<any>, options?: SimpleModalOptions): NgbModalRef {
    const modalRef: NgbModalRef = this.ngbModal.open(SimpleModalComponent);
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

  openConfirmationModal(header: string, type: SimpleModalType,
                        content: string | TemplateRef<any>, options?: SimpleModalOptions): NgbModalRef {
    const modalOptions: SimpleModalOptions = {
      isInformationOnly: false,
      confirmMessage: 'Yes',
      cancelMessage: 'No, cancel the operation',
      ...options,
    };
    return this.open(header, type, content, modalOptions);
  }

  openInformationModal(header: string, type: SimpleModalType,
                       content: string | TemplateRef<any>, options?: SimpleModalOptions): NgbModalRef {
    const modalOptions: SimpleModalOptions = {
      isInformationOnly: true,
      confirmMessage: 'OK',
      ...options,
    };
    return this.open(header, type, content, modalOptions);
  }
}
