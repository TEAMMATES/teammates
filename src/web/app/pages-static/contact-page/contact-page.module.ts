import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ContactPageComponent } from './contact-page.component';

/**
 * Module for contact page.
 */
@NgModule({
  declarations: [
    ContactPageComponent,
  ],
  exports: [
    ContactPageComponent,
  ],
  imports: [
    CommonModule,
  ],
})
export class ContactPageModule { }
