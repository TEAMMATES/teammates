import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../../environments/environment';

/**
 * Getting Started Section for Instructors
 */
@Component({
  selector: 'tm-instructor-help-getting-started',
  templateUrl: './instructor-help-getting-started.component.html',
  styleUrls: ['./instructor-help-getting-started.component.scss'],
})
export class InstructorHelpGettingStartedComponent implements OnInit {

  readonly supportEmail: string = environment.supportEmail;
  instructorHelpPath: string = '';

  constructor(private route: ActivatedRoute) {
    let r: ActivatedRoute = this.route;
    while (r.firstChild) {
      r = r.firstChild;
    }
    r.data.subscribe((resp: any) => {
      this.instructorHelpPath = resp.instructorHelpPath;
    });
  }

  ngOnInit(): void {
  }

  /**
   * To scroll to a specific HTML id
   */
  jumpTo(target: string): boolean {
    const destination: Element | null = document.getElementById(target);
    if (destination) {
      destination.scrollIntoView();
      // to prevent the navbar from covering the text
      window.scrollTo(0, window.pageYOffset - 50);
    }
    return false;
  }

}
