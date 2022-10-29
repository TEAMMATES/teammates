import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { InstructorSearchComponentsModule } from './instructor-search-components.module';
import { InstructorSearchPageComponent } from './instructor-search-page.component';

const routes: Routes = [
  {
    path: '',
    component: InstructorSearchPageComponent,
  },
];

/**
 * Module for instructor search page.
 */
@NgModule({
  declarations: [
    InstructorSearchPageComponent,
  ],
  exports: [
    InstructorSearchPageComponent,
  ],
  imports: [
    CommonModule,
    InstructorSearchComponentsModule,
    RouterModule.forChild(routes),
    LoadingSpinnerModule,
  ],
})
export class InstructorSearchPageModule { }
