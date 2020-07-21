import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import {
  CustomPrivilegeSettingPanelComponent,
} from './custom-privilege-setting-panel/custom-privilege-setting-panel.component';
import { InstructorCourseEditPageComponent } from './instructor-course-edit-page.component';
import { InstructorEditPanelComponent } from './instructor-edit-panel/instructor-edit-panel.component';
import { InstructorRoleDescriptionPipe } from './instructor-edit-panel/instructor-role-description.pipe';
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
    InstructorRoleDescriptionPipe,
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
  ],
  entryComponents: [
    ViewRolePrivilegesModalComponent,
  ],
})
export class InstructorCourseEditPageModule { }
