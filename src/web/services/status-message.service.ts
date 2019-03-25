import { Injectable } from '@angular/core';
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
      duration: 5000,
      verticalPosition: 'top',
      panelClass: ['snackbar', message.color],
    });
  }

}
