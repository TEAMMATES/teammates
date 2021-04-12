import { Component, OnInit } from '@angular/core';

import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../../../environments/environment';

import { AuthService } from '../../../services/auth.service';
import { AuthInfo, Gender, MessageOutput, Nationalities, StudentProfile } from '../../../types/api-output';

import { FormControl, FormGroup } from '@angular/forms';

import { StatusMessageService } from '../../../services/status-message.service';
import { StudentProfileService } from '../../../services/student-profile.service';
import { ErrorMessageOutput } from '../../error-message-output';

import { from, throwError } from 'rxjs';
import { catchError, finalize, switchMap } from 'rxjs/operators';
import { NationalitiesService } from '../../../services/nationalities.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { UploadEditProfilePictureModalComponent } from './upload-edit-profile-picture-modal/upload-edit-profile-picture-modal.component';

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
  id: string = '';
  student!: StudentProfile;
  name?: string;
  editForm!: FormGroup;
  nationalities?: string[];
  profilePicLink!: string;
  defaultPictureLink: string = '/assets/images/profile_picture_default.png';

  isLoadingStudentProfile: boolean = false;
  hasLoadingStudentProfileFailed: boolean = false;
  isSavingProfileEdit: boolean = false;

  private backendUrl: string = environment.backendUrl;

  constructor(private simpleModalService: SimpleModalService,
              private nationalitiesService: NationalitiesService,
              private authService: AuthService,
              private ngbModal: NgbModal,
              private statusMessageService: StatusMessageService,
              private studentProfileService: StudentProfileService) {
  }

  ngOnInit(): void {
    // populate drop-down menu for nationality list
    this.initNationalities();
    this.loadStudentProfile();
  }

  /**
   * Fetches the list of nationalities needed for the drop down box.
   */
  initNationalities(): void {
    this.nationalitiesService.getNationalities().subscribe((response: Nationalities) => {
      this.nationalities = response.nationalities;
    });
  }

  /**
   * Loads the student profile details for this page.
   */
  loadStudentProfile(): void {
    this.hasLoadingStudentProfileFailed = false;
    this.isLoadingStudentProfile = true;
    this.authService.getAuthUser().subscribe((auth: AuthInfo) => {
      if (auth.user) {
        this.id = auth.user.id;

        this.profilePicLink = `${this.backendUrl}/webapi/student/profilePic?user=${this.id}`;

        // retrieve profile once we have the student's googleId
        this.studentProfileService.getStudentProfile()
            .pipe(finalize(() => this.isLoadingStudentProfile = false))
            .subscribe((response: StudentProfile) => {
              if (response) {
                this.student = response;
                this.name = response.name;
                this.initStudentProfileForm(this.student);
              } else {
                this.hasLoadingStudentProfileFailed = true;
                this.statusMessageService.showErrorToast('Error retrieving student profile');
              }
            }, (response: ErrorMessageOutput) => {
              this.hasLoadingStudentProfileFailed = true;
              this.statusMessageService.showErrorToast(response.error.message);
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
  onSubmit(): void {
    const modalRef: NgbModalRef = this.simpleModalService
        .openConfirmationModal('Save Changes?', SimpleModalType.INFO, 'Are you sure you want to make changes to your profile?');
    modalRef.result.then(() => this.submitEditForm(), () => {});
  }

  /**
   * Opens a modal box to upload/edit profile picture.
   */
  onUploadEdit(): void {
    const NO_IMAGE_UPLOADED: number = 600;

    this.studentProfileService.getProfilePicture()
        .pipe(
            // Open Modal and wait for user to upload picture
            switchMap((image: Blob | null) => {
              const modalRef: NgbModalRef = this.ngbModal.open(UploadEditProfilePictureModalComponent);
              modalRef.componentInstance.image = image;

              return from(modalRef.result);
            }),
            // If no image is uploaded, throw an error
            catchError(() => throwError({
              error: {
                message: 'No image uploaded',
              },
              status: NO_IMAGE_UPLOADED,
            })),
            // Post the form data
            switchMap((formData: FormData) => {
              return this.studentProfileService.postProfilePicture(formData);
            }),
        )
        // Display message status
        .subscribe(() => {
          this.statusMessageService.showSuccessToast('Your profile picture has been saved successfully');

          // Force reload
          const timestamp: number = (new Date()).getTime();
          this.profilePicLink = `${this.backendUrl}/webapi/student/profilePic?${timestamp}&user=${this.id}`;
        }, (response: ErrorMessageOutput) => {
          // If the error was due to not image uploaded, do nothing
          if (response.status === NO_IMAGE_UPLOADED) {
            return;
          }

          this.statusMessageService.showErrorToast(response.error.message);
        });
  }

  /**
   * Submits the form data to edit the student profile details.
   */
  submitEditForm(): void {
    this.isSavingProfileEdit = true;
    this.studentProfileService.updateStudentProfile(this.id, {
      shortName: this.editForm.controls.studentshortname.value,
      email: this.editForm.controls.studentprofileemail.value,
      institute: this.editForm.controls.studentprofileinstitute.value,
      nationality: this.editForm.controls.studentnationality.value,
      gender: this.editForm.controls.studentgender.value,
      moreInfo: this.editForm.controls.studentprofilemoreinfo.value,
      existingNationality: this.editForm.controls.existingNationality.value,
    }).pipe(finalize(() => this.isSavingProfileEdit = false)).subscribe((response: MessageOutput) => {
      if (response) {
        this.statusMessageService.showSuccessToast(response.message);
      }
    }, (response: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(`Could not save your profile! ${response.error.message}`);
    });
  }

  /**
   * Prompts the user with a modal box to confirm deleting the profile picture.
   */
  onDelete(): void {
    const modalRef: NgbModalRef = this.simpleModalService
        .openConfirmationModal('Delete profile picture?', SimpleModalType.DANGER,
        'Warning: Profile picture cannot be recovered.');
    modalRef.result.then(() => {
      this.deleteProfilePicture();
    }, () => {});
  }

  /**
   * Deletes the profile picture.
   */
  deleteProfilePicture(): void {
    const paramMap: Record<string, string> = {
      googleid: this.id,
    };
    this.studentProfileService.deleteProfilePicture(paramMap)
        .subscribe((response: MessageOutput) => {
          if (response) {
            this.statusMessageService.showSuccessToast(response.message);
            this.profilePicLink = '/assets/images/profile_picture_default.png';
          }
        }, (response: ErrorMessageOutput) => {
          this.statusMessageService.
            showErrorToast(`Could not delete your profile picture! ${response.error.message}`);
        });
  }

  /**
   * Sets the profile picture of a student as the default image.
   */
  setDefaultPic(): void {
    this.profilePicLink = this.defaultPictureLink;
  }
}
