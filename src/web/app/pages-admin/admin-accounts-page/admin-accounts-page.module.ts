import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminAccountsPageComponent } from './admin-accounts-page.component';


const routes: Routes = [
  {
    path: '',
    component: AdminAccountsPageComponent,
  },
];

/**
 * Module for admin accounts page.
 */
@NgModule({
  exports: [
    AdminAccountsPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    AdminAccountsPageComponent,
],
})
export class AdminAccountsPageModule { }
