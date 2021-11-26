import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../environments/environment';
import { AuthService } from '../services/auth.service';
import { CourseService } from '../services/course.service';
import { NavigationService } from '../services/navigation.service';
import { AuthInfo, JoinStatus } from '../types/api-output';
import { ErrorReportComponent } from './components/error-report/error-report.component';
import { ErrorMessageOutput } from './error-message-output';

/**
 * User join page component.
 */
@Component({
  selector: 'tm-user-join-page',
  templateUrl: './user-join-page.component.html',
  styleUrls: ['./user-join-page.component.scss'],
})
export class UserJoinPageComponent implements OnInit {

  isLoading: boolean = true;
  hasJoined: boolean = false;
  validUrl: boolean = true;
  entityType: string = '';
  key: string = '';
  userId: string = '';

  private backendUrl: string = environment.backendUrl;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private courseService: CourseService,
              private navigationService: NavigationService,
              private authService: AuthService,
              private ngbModal: NgbModal) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.entityType = queryParams.entitytype;
      this.key = queryParams.key;

      const nextUrl: string = `${window.location.pathname}${window.location.search.replace(/&/g, '%26')}`;
      this.authService.getAuthUser(undefined, nextUrl).subscribe((auth: AuthInfo) => {
        if (!auth.user) {
          this.isLoading = false;
          if (this.entityType === 'student') {
            window.location.href = `${this.backendUrl}${auth.studentLoginUrl}`;
          } else if (this.entityType === 'instructor') {
            window.location.href = `${this.backendUrl}${auth.instructorLoginUrl}`;
          }
          return;
        }
        this.userId = auth.user.id;
        this.courseService.getJoinCourseStatus(this.key, this.entityType).subscribe((resp: JoinStatus) => {
          this.hasJoined = resp.hasJoined;
          if (this.hasJoined) {
            // The regkey has been used; simply redirect the user to their home page,
            // regardless of whether the regkey matches or not.
            this.navigationService.navigateByURL(this.router, `/web/${this.entityType}/home`);
          } else {
            this.isLoading = false;
          }
        }, (resp: ErrorMessageOutput) => {
          if (resp.status === 404) {
            this.validUrl = false;
            this.isLoading = false;
            return;
          }
          const modalRef: any = this.ngbModal.open(ErrorReportComponent);
          modalRef.componentInstance.requestId = resp.error.requestId;
          modalRef.componentInstance.errorMessage = resp.error.message;
        });
      });
    });
  }

  /**
   * Joins the course.
   */
  joinCourse(): void {
    this.courseService.joinCourse(this.key, this.entityType).subscribe(() => {
      this.navigationService.navigateByURL(this.router, `/web/${this.entityType}`);
    }, (resp: ErrorMessageOutput) => {
      const modalRef: any = this.ngbModal.open(ErrorReportComponent);
      modalRef.componentInstance.requestId = resp.error.requestId;
      modalRef.componentInstance.errorMessage = resp.error.message;
    });
  }

}
