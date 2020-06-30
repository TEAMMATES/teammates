import { Component, EventEmitter, Input, OnInit, Output, TemplateRef } from '@angular/core';
import { Toast } from './toast';

/**
 * Displays status messages as toasts.
 */
@Component({
  selector: 'tm-toast',
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.scss'],
})
export class ToastComponent implements OnInit {

  @Input() toast: Toast | null = null;
  @Output() toastChange: EventEmitter<Toast | null> = new EventEmitter<Toast | null>();

  constructor() { }

  ngOnInit(): void {
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
