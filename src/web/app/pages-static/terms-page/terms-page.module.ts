import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TermsPageComponent } from './terms-page.component';

/**
 * Module for terms page.
 */
@NgModule({
  declarations: [
    TermsPageComponent,
  ],
  exports: [
    TermsPageComponent,
  ],
  imports: [
    CommonModule,
  ],
})
export class TermsPageModule { }
