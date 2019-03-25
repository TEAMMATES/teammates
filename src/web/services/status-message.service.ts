import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material';
import { Observable, Subject } from 'rxjs';
import { StatusMessage } from '../app/components/status-message/status-message';

/**
 * Handles operations related to status message provision.
 */
@Injectable({
  providedIn: 'root',
})
export class StatusMessageService {

  private alertEvent: Subject<StatusMessage> = new Subject();

  constructor(private snackBar: MatSnackBar) {}

  /**
   * Gets the observable event of status messages.
   */
  getAlertEvent(): Observable<StatusMessage> {
    return this.alertEvent.asObservable();
  }

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
    // this.alertEvent.next(message);
    this.snackBar.open(message.message, '', {
      duration: 5000,
      verticalPosition: 'top',
      panelClass: ['snackbar', message.color],
    });
  }

}
