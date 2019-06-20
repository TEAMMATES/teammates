import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpRequestService } from '../../../services/http-request.service';

import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../../../environments/environment';

import { AuthService } from '../../../services/auth.service';
import { AuthInfo, MessageOutput, Nationalities, StudentProfile } from '../../../types/api-output';

import { FormControl, FormGroup } from '@angular/forms';

import { StatusMessageService } from '../../../services/status-message.service';
import { StudentProfileService } from '../../../services/student-profile.service';
import { Gender } from '../../../types/gender';
import { ErrorMessageOutput } from '../../error-message-output';

import {
  UploadEditProfilePictureModalComponent,
} from './upload-edit-profile-picture-modal/upload-edit-profile-picture-modal.component';

/**
 * Student profile page.
 */
@Component({
  selector: 'tm-student-profile-page',
  templateUrl: './student-profile-page.component.html',
  styleUrls: ['./student-profile-page.component.scss'],
})
export class StudentProfilePageComponent implements OnInit {

  Gender: typeof Gender = Gender; // enum
  user: string = '';
  id: string = '';
  student!: StudentProfile;
  name?: string;
  editForm!: FormGroup;
  nationalities?: string[];
  profilePicLink!: string;
  currentTime?: number;
  defaultPictureLink: string = '/assets/images/profile_picture_default.png';

  private backendUrl: string = environment.backendUrl;

  constructor(private route: ActivatedRoute,
              private ngbModal: NgbModal,
              private httpRequestService: HttpRequestService,
              private authService: AuthService,
              private statusMessageService: StatusMessageService,
              private studentProfileService: StudentProfileService) {
  }

  ngOnInit(): void {
    // populate drop-down menu for nationality list
    this.initNationalities();

    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;

      this.profilePicLink = `${this.backendUrl}/webapi/student/profilePic`;
      this.loadStudentProfile();
    });
  }

  /**
   * Fetches the list of nationalities needed for the drop down box.
   */
  initNationalities(): void {
    this.httpRequestService.get('/nationalities').subscribe((response: Nationalities) => {
      this.nationalities = response.nationalities;
    });
  }

  /**
   * Loads the student profile details for this page.
   */
  loadStudentProfile(): void {
    this.authService.getAuthUser().subscribe((auth: AuthInfo) => {
      if (auth.user) {
        this.id = auth.user.id;

        // retrieve profile once we have the student's googleId
        this.studentProfileService.getStudentProfile().subscribe((response: StudentProfile) => {
          if (response) {
            this.student = response;
            this.name = response.name;
            this.initStudentProfileForm(this.student);
          } else {
            this.statusMessageService.showErrorMessage('Error retrieving student profile');
          }
        }, (response: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(response.error.message);
        });
      }
    });
  }

  /**
   * Initializes the edit form with the student profile fields fetched from the backend.
   */
  initStudentProfileForm(profile: StudentProfile): void {
    this.editForm = new FormGroup({
      studentshortname: new FormControl(profile.shortName),
      studentprofileemail: new FormControl(profile.email),
      studentprofileinstitute: new FormControl(profile.institute),
      studentnationality: new FormControl(profile.nationality),
      existingNationality: new FormControl(profile.nationality),
      studentgender: new FormControl(profile.gender),
      studentprofilemoreinfo: new FormControl(profile.moreInfo),
    });
  }

  /**
   * Prompts the user with a modal box to confirm changes made to the form.
   */
  onSubmit(confirmEditProfile: any): void {
    this.ngbModal.open(confirmEditProfile);
  }

  /**
   * Opens a modal box to upload/edit profile picture.
   */
  onUploadEdit(): void {
    const modalRef: NgbModalRef = this.ngbModal.open(UploadEditProfilePictureModalComponent);
    modalRef.componentInstance.profilePicLink = this.profilePicLink;
    modalRef.result.then((formData: FormData) => {
      if (!formData) {
        this.statusMessageService.showWarningMessage('No photo uploaded');
        return;
      }
      const paramsMap: { [key: string]: string } = {
        user: this.user,
      };

      this.httpRequestService.post('/student/profilePic', paramsMap, formData)
          .subscribe(() => {
            this.statusMessageService.showSuccessMessage('Your profile picture has been saved successfully');

            // force reload
            const timestamp: number = (new Date()).getTime();
            this.profilePicLink = `${this.backendUrl}/webapi/student/profilePic?${timestamp}`;
          }, (response: ErrorMessageOutput) => {
            this.statusMessageService.showErrorMessage(response.error.message);
          });
    }, () => {});
  }

  /**
   * Submits the form data to edit the student profile details.
   */
  submitEditForm(): void {
    this.studentProfileService.updateStudentProfile(this.user, this.id, {
      shortName: this.editForm.controls.studentshortname.value,
      email: this.editForm.controls.studentprofileemail.value,
      institute: this.editForm.controls.studentprofileinstitute.value,
      nationality: this.editForm.controls.studentnationality.value,
      gender: this.editForm.controls.studentgender.value,
      moreInfo: this.editForm.controls.studentprofilemoreinfo.value,
      existingNationality: this.editForm.controls.existingNationality.value,
    }).subscribe((response: MessageOutput) => {
      if (response) {
        this.statusMessageService.showSuccessMessage(response.message);
      }
    }, (response: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(`Could not save your profile! ${response.error.message}`);
    });
  }

  /**
   * Prompts the user with a modal box to confirm deleting the profile picture.
   */
  onDelete(confirmDeleteProfilePicture: any): void {
    this.ngbModal.open(confirmDeleteProfilePicture);
  }

  /**
   * Deletes the profile picture and the profile picture key
   */
  deleteProfilePicture(): void {
    const paramMap: { [key: string]: string } = {
      user: this.user,
      googleid: this.id,
    };
    this.httpRequestService.delete('/student/profilePic', paramMap)
        .subscribe((response: MessageOutput) => {
          if (response) {
            this.statusMessageService.showSuccessMessage(response.message);
            this.profilePicLink = '/assets/images/profile_picture_default.png';
          }
        }, (response: ErrorMessageOutput) => {
          this.statusMessageService.
            showErrorMessage(`Could not delete your profile picture! ${response.error.message}`);
        });
  }

  /**
   * Sets the profile picture of a student as the default image.
   */
  setDefaultPic(): void {
    this.profilePicLink = this.defaultPictureLink;
  }
}
