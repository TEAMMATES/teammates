import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ContactUsFormModule } from '../../components/contact-us-form/contact-us-form.module';
import { ContactPageComponent } from './contact-page.component';

const routes: Routes = [
  {
    path: '',
    component: ContactPageComponent,
  },
];

/**
 * Module for contact page.
 */
@NgModule({
    declarations: [
        ContactPageComponent,
    ],
    exports: [
        ContactPageComponent,
    ],
    imports: [
        CommonModule,
        RouterModule.forChild(routes),
        ContactUsFormModule,
    ],
})
export class ContactPageModule { }
