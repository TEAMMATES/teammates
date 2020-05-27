import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AdminSearchPageComponent } from './admin-search-page.component';
import {
  RegenerateLinksConfirmModalComponent,
} from './regenerate-links-confirm-modal/regenerate-links-confirm-modal.component';

/**
 * Module for admin search page.
 */
@NgModule({
  declarations: [
    AdminSearchPageComponent,
    RegenerateLinksConfirmModalComponent,
  ],
  exports: [
    AdminSearchPageComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
  ],
  entryComponents: [
    RegenerateLinksConfirmModalComponent,
  ],
})
export class AdminSearchPageModule { }
