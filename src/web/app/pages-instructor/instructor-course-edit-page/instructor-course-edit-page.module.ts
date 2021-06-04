import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';
import {
  CustomPrivilegeSettingPanelComponent,
} from './custom-privilege-setting-panel/custom-privilege-setting-panel.component';
import { InstructorCourseEditPageComponent } from './instructor-course-edit-page.component';
import { InstructorEditPanelComponent } from './instructor-edit-panel/instructor-edit-panel.component';
import { ViewRolePrivilegesModalComponent } from './view-role-privileges-modal/view-role-privileges-modal.component';

const routes: Routes = [
  {
    path: '',
    component: InstructorCourseEditPageComponent,
  },
];

/**
 * Module for instructor course edit page component.
 */
@NgModule({
  declarations: [
    InstructorCourseEditPageComponent,
    InstructorEditPanelComponent,
    ViewRolePrivilegesModalComponent,
    CustomPrivilegeSettingPanelComponent,
  ],
  exports: [
    InstructorCourseEditPageComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule.forChild(routes),
    NgbTooltipModule,
    TeammatesCommonModule,
    LoadingSpinnerModule,
    AjaxLoadingModule,
    LoadingRetryModule,
    TeammatesRouterModule,
  ],
  entryComponents: [
    ViewRolePrivilegesModalComponent,
  ],
})
export class InstructorCourseEditPageModule { }
