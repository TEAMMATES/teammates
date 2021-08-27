import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoadingSpinnerModule } from './components/loading-spinner/loading-spinner.module';
import { UserCreateAccountPageComponent } from './user-create-account-page.component';

const routes: Routes = [
  {
    path: '',
    component: UserCreateAccountPageComponent,
  },
];

/**
 * Module for user create account page.
 */
@NgModule({
  declarations: [
    UserCreateAccountPageComponent,
  ],
  exports: [
    UserCreateAccountPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    LoadingSpinnerModule
  ],
})
export class UserCreateAccountPageModule { }
