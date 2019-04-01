import { Injectable, TemplateRef } from '@angular/core';
import { MatSnackBar } from '@angular/material';
import { StatusMessage } from '../app/components/status-message/status-message';

/**
 * Handles operations related to status message provision.
 */
@Injectable({
  providedIn: 'root',
})
export class StatusMessageService {

  constructor(private snackBar: MatSnackBar) {}

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

}
