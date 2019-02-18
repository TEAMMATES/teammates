import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpRequestService } from '../../../services/http-request.service';

import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../../../environments/environment';

import { AuthService } from '../../../services/auth.service';
import { AuthInfo, MessageOutput, Nationalities } from '../../../types/api-output';

import { FormControl, FormGroup } from '@angular/forms';

import { StatusMessageService } from '../../../services/status-message.service';
import { Gender } from '../../../types/gender';
import { StudentProfileService } from '../../../services/student-profile.service';
import { StudentProfileUpdateRequest } from '../../../types/api-request';
import { ErrorMessageOutput } from '../../error-message-output';

interface StudentProfile {
  shortName: string;
  email: string;
  institute: string;
  nationality: string;
  gender: Gender;
  moreInfo: string;
  pictureKey: string;
}

interface StudentDetails {
  studentProfile: StudentProfileUpdateRequest;
  name: string;
  requestId: string;
}


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
  student?: StudentDetails;
  name?: string;
  editForm!: FormGroup;
  nationalities?: string[];

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
      this.loadStudentProfile();
    });
  }

  /**
   * Construct the url for the profile picture from the given key.
   */
  getProfilePictureUrl(pictureKey: string): string {
    if (!pictureKey) {
      return '/assets/images/profile_picture_default.png';
    }
    return `${this.backendUrl}/students/profilePic?blob-key=${pictureKey}`;
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
        const paramMap: { [key: string]: string } = {
          user: this.user,
          googleid: auth.user.id,
        };

        // retrieve profile once we have the student's googleId
        this.studentProfileService.getStudentProfile(paramMap).subscribe((response: StudentDetails) => {
          if (response) {
            this.student = response;
            this.name = response.name;
            this.initStudentProfileForm(this.student.studentProfile);
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
  initStudentProfileForm(profile: StudentProfileUpdateRequest): void {
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
   * Submits the form data to edit the student profile details.
   */
  submitEditForm(): void {
    const paramsMap: { [key: string]: string } = {
      user: this.user,
      googleid: this.id,
    };

    this.studentProfileService.updateStudentProfile(paramsMap, {
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
}
