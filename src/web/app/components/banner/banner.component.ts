import { Component } from '@angular/core';
import { environment } from '../../../environments/environment';
import InstructorBannerContent, {
  InstructorBannerContentType,
} from './instructor.banner-content';
import StudentBannerContent, {
  StudentBannerContentType,
} from './student.banner-content';
/**
 * Version redirect banner.
 */
@Component({
  selector: 'tm-banner',
  templateUrl: './banner.component.html',
  styleUrls: ['./banner.component.scss'],
})
export class BannerComponent {
  studentBannerContent: number;
  instructorBannerContent: number;
  studentBannerData: StudentBannerContentType | undefined;
  instructorBannerData: InstructorBannerContentType | undefined;

  constructor() {
    this.studentBannerContent = environment.studentBannerContent;
    this.instructorBannerContent = environment.instructorBannerContent;

    if (this.studentBannerContent === 1) {
      try {
        this.studentBannerData = StudentBannerContent;
      } catch (e) {
        this.studentBannerData = undefined;
      }
    }
    if (this.instructorBannerContent === 1) {
      try {
        this.instructorBannerData = InstructorBannerContent;
      } catch (e) {
        this.instructorBannerData = undefined;
      }
    }
  }
  closeBanner = () => {
    this.studentBannerContent = 0;
    this.instructorBannerContent = 0;
  }
}
