import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbToastModule } from '@ng-bootstrap/ng-bootstrap';
import { ToastComponent } from './toast.component';

/**
 * Module for toasts.
 */
@NgModule({
  exports: [
    ToastComponent,
  ],
  imports: [
    CommonModule,
    NgbToastModule,
    ToastComponent,
  ],
})
export class ToastModule { }
