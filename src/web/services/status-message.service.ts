import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { StatusMessage } from '../app/components/status-message/status-message';

/**
 * Handles operations related to status message provision.
 */
@Injectable({
  providedIn: 'root',
})
export class StatusMessageService {

  static readonly COLOR_ERROR: string = 'danger';
  static readonly COLOR_WARNING: string = 'warning';
  static readonly COLOR_SUCCESS: string = 'success';

  private alertEvent: Subject<StatusMessage> = new Subject();

  constructor() {}

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
      color: StatusMessageService.COLOR_SUCCESS,
    });
  }

  /**
   * Shows a warning message on the page.
   */
  showWarningMessage(message: string): void {
    this.showMessage({
      message,
      color: StatusMessageService.COLOR_WARNING,
    });
  }

  /**
   * Shows an error message on the page.
   */
  showErrorMessage(message: string): void {
    this.showMessage({
      message,
      color: StatusMessageService.COLOR_ERROR,
    });
  }

  private showMessage(message: StatusMessage): void {
    this.alertEvent.next(message);
  }

}
