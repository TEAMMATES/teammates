import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { AdminTimezonePageComponent } from './admin-timezone-page.component';

/**
 * Module for admin timezone page.
 */
@NgModule({
  declarations: [
    AdminTimezonePageComponent,
  ],
  exports: [
    AdminTimezonePageComponent,
  ],
  imports: [
    CommonModule,
  ],
})
export class AdminTimezonePageModule { }
