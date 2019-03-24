import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { Snackbar } from '../app/components/snackbar/snackbar';

/**
 * Handles operations related to status message provision.
 */
@Injectable({
  providedIn: 'root',
})
export class StatusMessageService {

  private alertEvent: Subject<Snackbar> = new Subject();

  constructor() {}

  /**
   * Gets the observable event of status messages.
   */
  getAlertEvent(): Observable<Snackbar> {
    return this.alertEvent.asObservable();
  }

  /**
   * Shows a success message on the page.
   */
  showSuccessMessage(message: string): void {
    this.showMessage({
      message,
      color: 'success',
    });
  }

  /**
   * Shows a warning message on the page.
   */
  showWarningMessage(message: string): void {
    this.showMessage({
      message,
      color: 'warning',
    });
  }

  /**
   * Shows an error message on the page.
   */
  showErrorMessage(message: string): void {
    this.showMessage({
      message,
      color: 'danger',
    });
  }

  private showMessage(message: Snackbar): void {
    this.alertEvent.next(message);
  }

}
