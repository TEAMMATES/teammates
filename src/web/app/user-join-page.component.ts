import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CourseService } from '../services/course.service';
import { HttpRequestService } from '../services/http-request.service';
import { JoinStatus } from '../types/api-output';
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
  entityType: string = '';
  key: string = '';
  institute: string = '';
  userId: string = '';

  constructor(private route: ActivatedRoute,
              private router: Router,
              private httpRequestService: HttpRequestService,
              private courseService: CourseService,
              private ngbModal: NgbModal) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.entityType = queryParams.entitytype;
      this.key = queryParams.key;
      this.institute = queryParams.instructorinstitution;

      this.courseService.joinCourse(this.key, this.entityType).subscribe((resp: JoinStatus) => {
        this.hasJoined = resp.hasJoined;
        this.userId = resp.userId || '';
        this.isLoading = false;
      }, (resp: ErrorMessageOutput) => {
        if (resp.status === 403) {
          this.isLoading = false;
        } else {
          const modalRef: any = this.ngbModal.open(ErrorReportComponent);
          modalRef.componentInstance.requestId = resp.error.requestId;
          modalRef.componentInstance.errorMessage = resp.error.message;
        }
      });
    });
  }

  /**
   * Joins the course.
   */
  joinCourse(): void {
    const paramMap: { [key: string]: string } = {
      key: this.key,
      entitytype: this.entityType,
      instructorinstitution: this.institute,
    };
    this.httpRequestService.put('/join', paramMap).subscribe(() => {
      this.router.navigate([`/web/${this.entityType}`]);
    }, (resp: ErrorMessageOutput) => {
      const modalRef: any = this.ngbModal.open(ErrorReportComponent);
      modalRef.componentInstance.requestId = resp.error.requestId;
      modalRef.componentInstance.errorMessage = resp.error.message;
    });
  }

}
