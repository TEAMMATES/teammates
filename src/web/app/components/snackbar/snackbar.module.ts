import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SnackbarComponent } from './snackbar.component';

/**
 * Module for snackbars.
 */
@NgModule({
  imports: [
    CommonModule,
  ],
  exports: [
    SnackbarComponent,
  ],
  declarations: [
    SnackbarComponent,
  ],
})
export class SnackbarModule { }
