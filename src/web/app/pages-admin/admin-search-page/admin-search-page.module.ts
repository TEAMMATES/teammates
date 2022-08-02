import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { AdminSearchPageComponent } from './admin-search-page.component';

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
})
export class AdminSearchPageModule { }
