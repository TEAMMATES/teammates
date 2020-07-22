import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { AdminTimezonePageComponent } from './admin-timezone-page.component';

const routes: Routes = [
  {
    path: '',
    component: AdminTimezonePageComponent,
  },
];

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
    RouterModule.forChild(routes),
    LoadingSpinnerModule,
  ],
})
export class AdminTimezonePageModule { }
