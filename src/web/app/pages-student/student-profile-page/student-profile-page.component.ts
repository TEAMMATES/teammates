import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpRequestService } from '../../../services/http-request.service';

import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../../../environments/environment';

import { AuthService } from '../../../services/auth.service';
import { AuthInfo, MessageOutput } from '../../../types/api-output';

import { FormControl, FormGroup } from '@angular/forms';

import { StatusMessageService } from '../../../services/status-message.service';
import { ErrorMessageOutput } from '../../error-message-output';

interface StudentProfile {
  shortName: string;
  email: string;
  institute: string;
  nationality: string;
  gender: string;
  moreInfo: string;
  pictureKey: string;
}

interface StudentDetails {
  studentProfile: StudentProfile;
  name: string;
  requestId: string;
}

interface NationalityData {
  nationalities: string[];
}

interface StudentProfileFormUrl {
  formUrl: string;
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

  user: string = '';
  id: string = '';
  student?: StudentDetails;
  name?: string;
  editForm!: FormGroup;
  nationalities?: string[];
  genders: string[] = ['male', 'female', 'other'];
  fileName: string = 'No File Selected';
  isFileSelected: boolean = false;
  formData?: FormData;

  private backendUrl: string = environment.backendUrl;

  constructor(private route: ActivatedRoute,
              private ngbModal: NgbModal,
              private httpRequestService: HttpRequestService,
              private authService: AuthService,
              private statusMessageService: StatusMessageService) {}

  ngOnInit(): void {
    // populate drop-down menu for nationality list
    this.initNationalities();

    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.loadStudentProfile();
    });
  }

  /**
   * Handles event(s) when a file is selected from the user's file browser.
   */
  onFileChanged(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      this.fileName = file.name;
      this.isFileSelected = true;

      this.formData = new FormData();
      this.formData.append('studentprofilephoto', file, file.name);
    }
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
    this.httpRequestService.get('/nationalities').subscribe((response: NationalityData) => {
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
        this.httpRequestService.get('/student/profile', paramMap).subscribe((response: StudentDetails) => {
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
   * Shows a modal box to upload/edit profile picture.
   */
  onUploadEdit(uploadEditPhoto: any): void {
    this.ngbModal.open(uploadEditPhoto);
  }

  uploadPicture(): void {
    const paramsMap: { [key: string]: string } = {
      user: this.user,
    };
    this.httpRequestService.post('/student/profileFormUrl', paramsMap)
        .subscribe((response: StudentProfileFormUrl) => {
          // This should work but there's an exception faced
          // java.lang.NullPointerException at
          // com.google.appengine.api.blobstore.dev.UploadBlobServlet.handleUpload(UploadBlobServlet.java:430)
/*          this.httpRequestService.postProfilePicture(response.formUrl, this.formData).subscribe((response: any) => {
            console.log(response);
          });*/

          /**
           * Errors faced by this call:
           * Request content type: multipart/form-data; boundary=----WebKitFormBoundaryXay94v2JiT1OE8yG
           * Request parts: [Part{n=studentprofilephoto,fn=apple-touch-icon-60x60.png,ct=image/png,s=937,t=true,f=null}]
           * Must be called from a blob upload callback request.
           * class java.lang.IllegalStateException
           */
          this.httpRequestService.post('/students/profilePic', paramsMap, this.formData)
              .subscribe((resp: any) => {
                console.log(resp);
              });
        }, (response: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(response.error.message);
        });
  }

  /**
   * Submits the form data to edit the student profile details.
   */
  submitEditForm(): void {
    const paramsMap: { [key: string]: string } = {
      user: this.user,
      googleid: this.id,
      ...this.editForm.value,
    };

    this.httpRequestService.put('/student/profile', paramsMap)
        .subscribe((response: MessageOutput) => {
          if (response) {
            this.statusMessageService.showSuccessMessage(response.message);
          }
        }, (response: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(`Could not save your profile! ${response.error.message}`);
        });
  }
}
