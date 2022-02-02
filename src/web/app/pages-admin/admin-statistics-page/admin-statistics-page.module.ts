import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { AdminStatisticsPageComponent } from './admin-statistics-page.component';

const routes: Routes = [
  {
    path: '',
    component: AdminStatisticsPageComponent,
  },
];

/**
 * Module for admin timezone page.
 */
@NgModule({
  declarations: [
    AdminStatisticsPageComponent,
  ],
  exports: [
    AdminStatisticsPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    LoadingSpinnerModule,
  ],
})
export class AdminStatisticsPageModule { }
