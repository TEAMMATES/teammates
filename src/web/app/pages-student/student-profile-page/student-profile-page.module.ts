import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ImageCropperModule } from 'ngx-image-cropper';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { GenderFormatPipe } from './student-profile-gender.pipe';
import { StudentProfilePageComponent } from './student-profile-page.component';
import {
  UploadEditProfilePictureModalComponent,
} from './upload-edit-profile-picture-modal/upload-edit-profile-picture-modal.component';

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
    RouterModule,
    TeammatesCommonModule,
    ImageCropperModule,
  ],
})
export class StudentProfilePageModule { }
