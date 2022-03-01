import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoadingSpinnerModule } from './components/loading-spinner/loading-spinner.module';
import { UserJoinPageComponent } from './user-join-page.component';

const routes: Routes = [
  {
    path: '',
    component: UserJoinPageComponent,
  },
];

/**
 * Module for user join page.
 */
@NgModule({
  declarations: [
    UserJoinPageComponent,
  ],
  exports: [
    UserJoinPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    LoadingSpinnerModule,
  ],
})
export class UserJoinPageModule { }
