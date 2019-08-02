import { Injectable } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { ModalType } from '../app/components/modal/modal-type';
import { ModalComponent } from '../app/components/modal/modal.component';

/**
 * Service to handle display of confirmation modals.
 * For more a complicated use case, use {@link NgbModal} directly.
 */
@Injectable({
  providedIn: 'root',
})
export class ModalService {

  constructor(private modalService: NgbModal) {
  }

  /**
   * Opens a modal
   * @param header to be displayed on the modal
   * @param type which determines the look of the modal
   * @param content to be displayed in the body of the modal. content supports HTML tags
   */
  open(header: string, type: ModalType, content: any): NgbModalRef {
    const modalRef: NgbModalRef = this.modalService.open(ModalComponent);
    modalRef.componentInstance.header = header;
    modalRef.componentInstance.content = content;
    modalRef.componentInstance.type = type;
    return modalRef;
  }
}
