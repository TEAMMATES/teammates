import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {
  SessionEditFormMode,
  SessionTemplate,
} from '../../components/session-edit-form/session-edit-form-model';

/**
 * Instructor feedback sessions list page.
 */
@Component({
  selector: 'tm-instructor-sessions-page',
  templateUrl: './instructor-sessions-page.component.html',
  styleUrls: ['./instructor-sessions-page.component.scss'],
})
export class InstructorSessionsPageComponent implements OnInit {

  // enum
  SessionEditFormMode: typeof SessionEditFormMode = SessionEditFormMode;

  user: string = '';

  coursesIdCandidates: string[] = [];
  sessionTemplates: SessionTemplate[] = [];

  constructor(private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
    });
  }

}
