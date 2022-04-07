import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { TimezonePageComponent } from './timezone-page.component';

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
  declarations: [
    TimezonePageComponent,
  ],
  exports: [
    TimezonePageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    LoadingSpinnerModule,
  ],
})
export class TimezonePageModule { }
