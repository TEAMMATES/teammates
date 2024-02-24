import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { AdminNotificationsPageComponent } from './admin-notifications-page.component';
import { NotificationEditFormComponent } from './notification-edit-form/notification-edit-form.component';
import { NotificationsTableComponent } from './notifications-table/notifications-table.component';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { DatepickerModule } from '../../components/datepicker/datepicker.module';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { RichTextEditorModule } from '../../components/rich-text-editor/rich-text-editor.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { TimepickerModule } from '../../components/timepicker/timepicker.module';

const routes: Routes = [
  {
    path: '',
    component: AdminNotificationsPageComponent,
  },
];

/**
 * Module for admin notifications page.
 */
@NgModule({
  declarations: [
    AdminNotificationsPageComponent,
    NotificationsTableComponent,
    NotificationEditFormComponent,
  ],
  exports: [
    AdminNotificationsPageComponent,
  ],
  imports: [
    AjaxLoadingModule,
    CommonModule,
    DatepickerModule,
    FormsModule,
    RichTextEditorModule,
    RouterModule.forChild(routes),
    LoadingSpinnerModule,
    LoadingRetryModule,
    NgbTooltipModule,
    TeammatesCommonModule,
    TimepickerModule,
  ],
})
export class AdminNotificationsPageModule { }
