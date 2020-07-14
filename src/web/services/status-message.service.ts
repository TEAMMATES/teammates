import { Injectable, TemplateRef } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { Observable, Subject } from 'rxjs';
import { StatusMessage } from '../app/components/status-message/status-message';
import {
  StatusMessageModalComponent,
} from '../app/components/status-message/status-messsage-modal/status-message-modal.component';
import { Toast } from '../app/components/toast/toast';

/**
 * Handles operations related to status message provision.
 */
@Injectable({
  providedIn: 'root',
})
export class StatusMessageService {

  private toast: Subject<Toast> = new Subject();

  constructor(private modalService: NgbModal) {}

  getToastEvent(): Observable<any> {
    return this.toast.asObservable();
  }

  /**
   * Shows a success toast on the page.
   */
  showSuccessToast(message: string, delay: number = 5000): void {
    this.showToast(message, 'bg-success text-light', delay);
  }

  /**
   * Shows a warning toast on the page.
   */
  showWarningToast(message: string, delay: number = 5000): void {
    this.showToast(message, 'bg-warning', delay);
  }

  /**
   * Shows an error toast on the page.
   */
  showErrorToast(message: string, delay: number = 10000): void {
    this.showToast(message, 'bg-danger text-light', delay);
  }

  private showToast(message: string, classes: string, delay: number): void {
    this.toast.next({
      message,
      classes,
      delay,
      autohide: true,
    });
  }

  /**
   * Shows a success toast containing HTML on the page
   */
  showSuccessToastTemplate(template: TemplateRef<any>, delay: number = 5000): void {
    this.showToastTemplate(template, 'bg-success text-light', delay);
  }

  private showToastTemplate(template: TemplateRef<any>, classes: string, delay: number): void {
    this.toast.next({
      classes,
      delay,
      message: template,
      autohide: true,
    });
  }

  /**
   * Shows a warning message modal on the page that must be acknowledged.
   */
  showWarningModal(title: string, subtitle: string, message: string): NgbModalRef {
    return this.showMessageModal(title, subtitle, { message, color: 'warning' });
  }

  private showMessageModal(title: string, subtitle: string, message: StatusMessage): NgbModalRef {
    const activeModal: NgbModalRef = this.modalService.open(StatusMessageModalComponent);
    activeModal.componentInstance.title = title;
    activeModal.componentInstance.subtitle = subtitle;
    activeModal.componentInstance.message = message.message;
    activeModal.componentInstance.color = message.color;
    return activeModal;
  }

}
