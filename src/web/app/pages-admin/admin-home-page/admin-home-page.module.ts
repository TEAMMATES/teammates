import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { AdminHomePageComponent } from './admin-home-page.component';
import {ResponseTimeSeriesChartModule} from "../../components/response-time-series-chart/response-time-series-chart.module";

const routes: Routes = [
  {
    path: '',
    component: AdminHomePageComponent,
  },
];

/**
 * Module for admin home page.
 */
@NgModule({
  declarations: [
    AdminHomePageComponent,
  ],
  exports: [
    AdminHomePageComponent,
  ],
    imports: [
        CommonModule,
        FormsModule,
        RouterModule.forChild(routes),
        AjaxLoadingModule,
        ResponseTimeSeriesChartModule,
    ],
})
export class AdminHomePageModule { }
