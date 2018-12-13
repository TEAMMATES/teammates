import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

/**
 * Instructor feedback session edit page.
 */
@Component({
  selector: 'tm-instructor-session-edit-page',
  templateUrl: './instructor-session-edit-page.component.html',
  styleUrls: ['./instructor-session-edit-page.component.scss'],
})
export class InstructorSessionEditPageComponent implements OnInit {

  user: string = '';

  constructor(private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
    });
  }

}
