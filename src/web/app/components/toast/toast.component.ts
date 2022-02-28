import { Component, EventEmitter, Input, OnChanges, Output, TemplateRef } from '@angular/core';
import { Toast } from './toast';

/**
 * Displays status messages as toasts.
 */
@Component({
  selector: 'tm-toast',
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.scss'],
})
export class ToastComponent implements OnChanges {

  @Input() toast: Toast | null = null;
  @Output() toastChange: EventEmitter<Toast | null> = new EventEmitter<Toast | null>();

  ngOnChanges(): void {
    // reset autohide timing
    this.setAutohide(false);
    setTimeout(() => {
      this.setAutohide(true);
    }, 100);
  }

  setAutohide(status: boolean): void {
    if (this.toast) {
      this.toast.autohide = status;
    }
  }

  /**
   * Removes the toast from view.
   */
  removeToast(): void {
    this.toast = null;
    this.toastChange.emit(null);
  }

  /**
   * Returns true if the argument passed is a TemplateRef.
   */
  isTemplate(): boolean {
    if (!this.toast) {
      return false;
    }
    return this.toast.message instanceof TemplateRef;
  }

}
