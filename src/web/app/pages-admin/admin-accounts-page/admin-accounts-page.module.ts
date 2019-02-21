import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AdminAccountsPageComponent } from './admin-accounts-page.component';

/**
 * Module for admin accounts page.
 */
@NgModule({
  declarations: [
    AdminAccountsPageComponent,
  ],
  exports: [
    AdminAccountsPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule,
  ],
})
export class AdminAccountsPageModule { }
