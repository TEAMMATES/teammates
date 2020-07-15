import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { AdminSearchPageComponent } from './admin-search-page.component';
import {
  RegenerateLinksConfirmModalComponent,
} from './regenerate-links-confirm-modal/regenerate-links-confirm-modal.component';
import {
  ResetGoogleIdConfirmModalComponent,
} from './reset-google-id-confirm-modal/reset-google-id-confirm-modal.component';

const routes: Routes = [
  {
    path: '',
    component: AdminSearchPageComponent,
  },
];

/**
 * Module for admin search page.
 */
@NgModule({
  declarations: [
    AdminSearchPageComponent,
    RegenerateLinksConfirmModalComponent,
    ResetGoogleIdConfirmModalComponent,
  ],
  exports: [
    AdminSearchPageComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbTooltipModule,
    RouterModule.forChild(routes),
  ],
  entryComponents: [
    RegenerateLinksConfirmModalComponent,
    ResetGoogleIdConfirmModalComponent,
  ],
})
export class AdminSearchPageModule { }
