import { Injectable } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { ModalTypes } from '../app/components/modal/modal-types';
import { ModalComponent } from '../app/components/modal/modal.component';

@Injectable({
  providedIn: 'root',
})
export class ModalService {

  constructor(private modalService: NgbModal) {
  }

  open(header: string, type: ModalTypes, content: any): NgbModalRef {
    const modalRef: NgbModalRef = this.modalService.open(ModalComponent);
    modalRef.componentInstance.header = header;
    modalRef.componentInstance.content = content;
    modalRef.componentInstance.type = type;
    return modalRef;
  }
}
