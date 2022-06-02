import { Injectable, TemplateRef } from '@angular/core';
import { NgbModal, NgbModalOptions, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
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
  onClosed?: () => void;
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
   * Opens a confirmation modal.
   *
   * @param header to be displayed on the modal
   * @param type which determines the look of the modal
   * @param content to be displayed in the body of the modal. content supports HTML tags
   * @param simpleModalOptions See {@code SimpleModalOptions}
   * @param ngbModalOptions See {@code NgbModalOptions}
   */
  private open(header: string, type: SimpleModalType, content: string | TemplateRef<any>,
      simpleModalOptions?: SimpleModalOptions, ngbModalOptions?: NgbModalOptions): NgbModalRef {
    const modalRef: NgbModalRef = this.ngbModal.open(SimpleModalComponent, ngbModalOptions);
    modalRef.componentInstance.header = header;
    modalRef.componentInstance.content = content;
    modalRef.componentInstance.type = type;
    if (simpleModalOptions) {
      Object.entries(simpleModalOptions).forEach(([key, value]: [string, string | boolean]) => {
        modalRef.componentInstance[key] = value;
      });
      if (simpleModalOptions.onClosed) {
        modalRef.closed.subscribe(() => {
          simpleModalOptions.onClosed!();
        });
        modalRef.dismissed.subscribe(() => {
          simpleModalOptions.onClosed!();
        });
      }
    }
    return modalRef;
  }

  openConfirmationModal(header: string, type: SimpleModalType, content: string | TemplateRef<any>,
      simpleModalOptions?: SimpleModalOptions, ngbModalOptions?: NgbModalOptions): NgbModalRef {
    const modalOptions: SimpleModalOptions = {
      isInformationOnly: false,
      confirmMessage: 'Yes',
      cancelMessage: 'No, cancel the operation',
      ...simpleModalOptions,
    };
    return this.open(header, type, content, modalOptions, ngbModalOptions);
  }

  openInformationModal(header: string, type: SimpleModalType, content: string | TemplateRef<any>,
      simpleModalOptions?: SimpleModalOptions, ngbModalOptions?: NgbModalOptions): NgbModalRef {
    const modalOptions: SimpleModalOptions = {
      isInformationOnly: true,
      confirmMessage: 'OK',
      ...simpleModalOptions,
    };
    return this.open(header, type, content, modalOptions, ngbModalOptions);
  }

  openLoadingModal(header: string, type: SimpleModalType, content: string | TemplateRef<any>,
      simpleModalOptions?: SimpleModalOptions, ngbModalOptions?: NgbModalOptions): NgbModalRef {
    const modalOptions: SimpleModalOptions = {
      isInformationOnly: true,
      confirmMessage: 'Abort',
      ...simpleModalOptions,
    };
    return this.open(header, type, content, modalOptions, ngbModalOptions);
  }
}
