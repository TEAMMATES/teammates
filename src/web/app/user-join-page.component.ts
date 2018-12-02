import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpRequestService } from '../services/http-request.service';
import { MessageOutput } from './message-output';

interface JoinStatus {
  hasJoined: boolean;
  userId?: string;
}

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
  errorMessage: string = '';
  entityType: string = '';
  key: string = '';
  institute: string = '';
  userId: string = '';

  constructor(private route: ActivatedRoute, private router: Router, private httpRequestService: HttpRequestService) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.entityType = queryParams.entitytype;
      this.key = queryParams.key;
      this.institute = queryParams.instructorinstitution;
      if (!this.entityType || !this.key) {
        this.errorMessage = 'Error: some required parameters are missing.';
        this.isLoading = false;
        return;
      }

      const paramMap: { [key: string]: string } = {
        key: this.key,
        entitytype: this.entityType,
      };
      this.httpRequestService.get('/join', paramMap).subscribe((resp: JoinStatus) => {
        this.hasJoined = resp.hasJoined;
        this.userId = resp.userId || '';
        this.isLoading = false;
      }, (resp: MessageOutput) => {
        this.errorMessage = resp.message;
        this.isLoading = false;
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
    }, (resp: MessageOutput) => {
      this.errorMessage = resp.message;
    });
  }

}
