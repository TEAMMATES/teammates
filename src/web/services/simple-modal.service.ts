import { Injectable, TemplateRef } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import {
  SimpleModalOptions,
  SimpleModalType,
  standardCancelButton,
  standardConfirmButton,
} from '../app/components/simple-modal/simple-modal';
import { SimpleModalComponent } from '../app/components/simple-modal/simple-modal.component';

const getButtonTypeFromModalType: Function = (type: SimpleModalType): string => {
  switch (type) {
    case SimpleModalType.DANGER:
      return 'btn-danger';
    case SimpleModalType.WARNING:
      return 'btn-warning';
    case SimpleModalType.INFO:
      return 'btn-info';
    case SimpleModalType.NEUTRAL:
      return 'btn-primary';
    default:
      return '';
  }
};

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
  open(options: SimpleModalOptions): NgbModalRef {
    const modalRef: NgbModalRef = this.ngbModal.open(SimpleModalComponent);
    modalRef.componentInstance.header = options.header;
    modalRef.componentInstance.content = options.content;
    modalRef.componentInstance.context = options.context || {};
    modalRef.componentInstance.type = options.type;
    modalRef.componentInstance.buttons = options.buttons || [];
    if (options.options) {
      Object.entries(options.options).map(([key, value]: [string, string | boolean]) => {
        modalRef.componentInstance[key] = value;
      });
    }
    return modalRef;
  }

  /**
   * Opens a simple confirmation modal with two yes/no type options.
   */
  openConfirmationModal(header: string, type: SimpleModalType, content: string | TemplateRef<any>,
                        action: Function, context: Record<string, any> = {}): NgbModalRef {
    return this.open({
      header,
      content,
      context,
      type,
      buttons: [
        standardCancelButton('No, cancel the operation'),
        standardConfirmButton(getButtonTypeFromModalType(type), action),
      ],
    });
  }

  /**
   * Opens a simple information modal with a standard "OK" button.
   */
  openInformationModal(header: string, type: SimpleModalType, content: string | TemplateRef<any>,
                       context: Record<string, any> = {}): NgbModalRef {
    return this.open({
      header,
      content,
      context,
      type,
      buttons: [
        standardConfirmButton(getButtonTypeFromModalType(type), () => {}, 'OK'),
      ],
    });
  }

}
