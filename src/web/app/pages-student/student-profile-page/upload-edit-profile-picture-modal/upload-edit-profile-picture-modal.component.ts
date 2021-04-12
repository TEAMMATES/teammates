import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ImageCroppedEvent, ImageCropperComponent } from 'ngx-image-cropper';

/**
 * Student profile page's modal to upload/edit photo.
 */
@Component({
  selector: 'tm-upload-edit-profile-picture-modal',
  templateUrl: './upload-edit-profile-picture-modal.component.html',
  styleUrls: ['./upload-edit-profile-picture-modal.component.scss'],
})
export class UploadEditProfilePictureModalComponent implements OnInit {
  imageChangedEvent: any = '';
  formData?: FormData;

  @ViewChild(ImageCropperComponent) imageCropper!: ImageCropperComponent;

  @Input() image!: Blob | null;

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
    if (this.image == null) {
      return;
    }
    this.blobToBase64Image(this.image);
  }

  /**
   * Converts the blob image into a base64 string to be shown in the image cropper.
   */
  blobToBase64Image(image: Blob): void {
    const reader: FileReader = new FileReader();
    reader.addEventListener('load', () => {
      if (reader.result) {
        this.imageCropper.imageBase64 = reader.result as string;
      }
    }, false);

    if (image) {
      reader.readAsDataURL(image);
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
   * Uploads the picture that has been newly uploaded/edited.
   */
  uploadPicture(): void {
    this.activeModal.close(this.formData);
  }

  /**
   * Handles event(s) when a file is selected from the user's file browser.
   */
  fileChangeEvent(event: any): void {
    this.imageChangedEvent = event;

    const file: File = event.target.files[0];
    if (file) {
      this.populateFormData(file);
    }
  }

  /**
   * Saves the latest cropped image.
   */
  imageCropped(event: ImageCroppedEvent): void {
    this.populateFormData(event.file as File);
  }

  /**
   * Rotates the image in the image cropper to the left.
   */
  rotateLeft(): void {
    this.imageCropper.rotateLeft();
  }

  /**
   * Rotates the image in the image cropper to the right.
   */
  rotateRight(): void {
    this.imageCropper.rotateRight();
  }

  /**
   * Flips the image in the image cropper horizontally.
   */
  flipHorizontal(): void {
    this.imageCropper.flipHorizontal();
  }

  /**
   * Flips the image in the image cropper vertically.
   */
  flipVertical(): void {
    this.imageCropper.flipVertical();
  }
}
