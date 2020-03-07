import { Injectable, TemplateRef } from '@angular/core';
import { MatSnackBar } from '@angular/material';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { StatusMessage } from '../app/components/status-message/status-message';
import {
  StatusMessageModalComponent,
} from '../app/components/status-message/status-messsage-modal/status-message-modal.component';

/**
 * Handles operations related to status message provision.
 */
@Injectable({
  providedIn: 'root',
})
export class StatusMessageService {

  constructor(private snackBar: MatSnackBar, private modalService: NgbModal) {}

  /**
   * Shows a success message on the page.
   */
  showSuccessMessage(message: string): void {
    this.showMessage({
      message,
      color: 'snackbar-success',
    });
  }

  /**
   * Shows a warning message on the page.
   */
  showWarningMessage(message: string): void {
    this.showMessage({
      message,
      color: 'snackbar-warning',
    });
  }

  /**
   * Shows an error message on the page.
   */
  showErrorMessage(message: string): void {
    this.showMessage({
      message,
      color: 'snackbar-danger',
    });
  }

  private showMessage(message: StatusMessage): void {
    this.snackBar.open(message.message, '', {
      duration: 10000,
      verticalPosition: 'top',
      panelClass: ['snackbar', message.color],
    });
  }

  /**
   * Shows a success message containing HTML on the page
   */
  showSuccessMessageTemplate(template: TemplateRef<any>): void {
    this.showTemplate(template, 'snackbar-success');
  }

  private showTemplate(template: TemplateRef<any>, color: string): void {
    this.snackBar.openFromTemplate(template, {
      duration: 10000,
      verticalPosition: 'top',
      panelClass: ['snackbar', color],
    });
  }

  /**
   * Shows a warning message modal on the page that must be acknowledged.
   */
  showWarningMessageModal(title: string, subtitle: string, message: string): NgbModalRef {
    return this.showMessageModal(title, subtitle, { message, color: 'warning' });
  }

  /**
   * Shows an error message modal on the page that must be acknowledged.
   */
  showErrorMessageModal(title: string, subtitle: string, message: string): NgbModalRef {
    return this.showMessageModal(title, subtitle, { message, color: 'danger' });
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
