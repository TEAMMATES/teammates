import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
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
  exports: [
    TimezonePageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    TimezonePageComponent,
],
})
export class TimezonePageModule { }
