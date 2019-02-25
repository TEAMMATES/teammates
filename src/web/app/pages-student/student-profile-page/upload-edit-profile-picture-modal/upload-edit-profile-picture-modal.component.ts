import { Component, EventEmitter, Input, OnInit, Output, QueryList, ViewChildren } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ImageCroppedEvent, ImageCropperComponent } from 'ngx-image-cropper';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { HttpRequestService } from '../../../../services/http-request.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { ErrorMessageOutput } from '../../../error-message-output';

import { StudentProfilePictureResults } from '../../../../types/api-output';

/**
 * Student profile page's modal to upload/edit photo.
 */
@Component({
  selector: 'tm-upload-edit-profile-picture-modal',
  templateUrl: './upload-edit-profile-picture-modal.component.html',
  styleUrls: ['./upload-edit-profile-picture-modal.component.scss'],
})
export class UploadEditProfilePictureModalComponent implements OnInit {

  isImageLoaded: boolean = false;
  user: string = '';
  fileName: string = 'No File Selected';
  isFileSelected: boolean = false;
  formData?: FormData;
  imageToShow: any;
  croppedImage: any;

  @ViewChildren(ImageCropperComponent) imageCropper!: QueryList<ImageCropperComponent>;
  @Input() pictureKey!: string;
  @Input() profilePicLink!: string;
  @Output() imageUpdated: EventEmitter<any> = new EventEmitter();

  private backendUrl: string = environment.backendUrl;

  constructor(public activeModal: NgbActiveModal,
              private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService) { }

  ngOnInit(): void {
    if (this.pictureKey) {
      this.getProfilePicture().subscribe((resp: any) => {
        this.blobToBase64Image(resp);
      });
      this.isImageLoaded = false;
    }
  }

  /**
   * Populates form data with the image blob.
   */
  populateFormData(file: File): void {
    this.formData = new FormData();
    this.formData.append('studentprofilephoto', file, file.name);
  }

  /**
   * Handles event(s) when a file is selected from the user's file browser.
   */
  onFileChanged(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      this.fileName = file.name;
      this.isFileSelected = true;
      this.populateFormData(file);
    }
  }

  /**
   * Uploads the picture that has been newly uploaded/edited.
   */
  uploadPicture(): void {
    const paramsMap: { [key: string]: string } = {
      user: this.user,
    };
    this.httpRequestService.post('/students/profilePic', paramsMap, this.formData)
        .subscribe((response: StudentProfilePictureResults) => {
          this.statusMessageService.showSuccessMessage('Your profile picture has been saved successfully');
          this.pictureKey = response.pictureKey;
          this.profilePicLink = `${this.backendUrl}/webapi/students/profilePic?blob-key=${this.pictureKey}`;

          // Gets the updated picture as blob to be filled in the image cropper
          this.getProfilePicture().subscribe((resp: any) => {
            this.blobToBase64Image(resp);
            this.imageUpdated.emit(this.pictureKey);
          });

          // Reset upload section
          this.fileName = 'No File Selected';
          this.isFileSelected = false;
        }, (response: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(response.error.message);
        });
  }

  /**
   * Triggers the appropriate actions when the 'Save Edited Photo' button is clicked.
   */
  saveEditedPhoto(): void {
    this.populateFormData(this.croppedImage);
    this.uploadPicture();
  }

  /**
   * Gets the profile picture as blob image.
   */
  getProfilePicture(): Observable<Blob> {
    const profilePicEndPoint: string =
        this.profilePicLink.replace(`${this.backendUrl}/webapi`, '');
    return this.httpRequestService.get(profilePicEndPoint, {}, 'blob');
  }

  /**
   * Converts the blob image into a base64 string to be shown in the image cropper.
   */
  blobToBase64Image(image: Blob): void {
    const reader: FileReader = new FileReader();
    reader.addEventListener('load', () => {
      this.imageToShow = reader.result;
      this.isImageLoaded = true;
    }, false);

    if (image) {
      reader.readAsDataURL(image);
    }
  }

  /**
   * Saves the latest cropped image.
   */
  imageCropped(event: ImageCroppedEvent): void {
    this.croppedImage = event.file;
  }

  /**
   * Rotates the image in the image cropper to the left.
   */
  rotateLeft(): void {
    this.imageCropper.last.rotateLeft();
  }

  /**
   * Rotates the image in the image cropper to the right.
   */
  rotateRight(): void {
    this.imageCropper.last.rotateRight();
  }

  /**
   * Flips the image in the image cropper horizontally.
   */
  flipHorizontal(): void {
    this.imageCropper.last.flipHorizontal();
  }

  /**
   * Flips the image in the image cropper vertically.
   */
  flipVertical(): void {
    this.imageCropper.last.flipVertical();
  }
}
