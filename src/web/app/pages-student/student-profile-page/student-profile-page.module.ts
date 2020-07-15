import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { ImageCropperModule } from 'ngx-image-cropper';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { GenderFormatPipe } from './student-profile-gender.pipe';
import { StudentProfilePageComponent } from './student-profile-page.component';
import {
  UploadEditProfilePictureModalComponent,
} from './upload-edit-profile-picture-modal/upload-edit-profile-picture-modal.component';

const routes: Routes = [
  {
    path: '',
    component: StudentProfilePageComponent,
  },
];

/**
 * Module for student profile page.
 */
@NgModule({
  declarations: [
    StudentProfilePageComponent,
    GenderFormatPipe,
    UploadEditProfilePictureModalComponent,
  ],
  exports: [
    StudentProfilePageComponent,
  ],
  entryComponents: [
    UploadEditProfilePictureModalComponent,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes),
    TeammatesCommonModule,
    ImageCropperModule,
    NgbTooltipModule,
    LoadingSpinnerModule,
    AjaxLoadingModule,
  ],
})
export class StudentProfilePageModule { }
