import { Component, Input, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../../../environments/environment';
import { StudentProfile } from './student-profile';

/**
 * A table displaying a details from a student's profile and a modal to view the more info field.
 */
@Component({
  selector: 'tm-student-profile',
  templateUrl: './student-profile.component.html',
  styleUrls: ['./student-profile.component.scss'],
})
export class StudentProfileComponent implements OnInit {

  @Input() studentProfile: StudentProfile | undefined;
  @Input() studentName: string = '';
  @Input() hideMoreInfo: boolean = false;

  private backendUrl: string = environment.backendUrl;

  constructor(private ngbModal: NgbModal) { }

  ngOnInit(): void {
  }

  /**
   * Construct the url for the profile picture from the given key.
   */
  getPictureUrl(pictureKey: string): string {
    if (!pictureKey) {
      return '/assets/images/profile_picture_default.png';
    }
    return `${this.backendUrl}/students/profilePic?blob-key=${pictureKey}`;
  }

  /**
   * Open the more info modal.
   */
  openModal(content: any): void {
    this.ngbModal.open(content);
  }

}
