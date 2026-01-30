import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TimezonePageComponent } from './timezone-page.component';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';

const routes: Routes = [
  {
    path: '',
    component: TimezonePageComponent,
  },
];

/**
 * Module for admin timezone page.
 */
@NgModule({
  exports: [
    TimezonePageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    LoadingSpinnerModule,
    TimezonePageComponent,
  ],
})
export class TimezonePageModule { }
