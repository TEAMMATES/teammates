import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TeammatesRouterDirective } from '../components/teammates-router/teammates-router.directive';

@Component({
  selector: 'tm-unauthorized-warning-page',
  styleUrls: ['./unauthorized-warning-page.component.scss'],
  templateUrl: './unauthorized-warning-page.component.html',
  imports: [TeammatesRouterDirective],
})
export class UnauthorizedWarningPageComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);

  role = '';
  reason = '';

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.role = queryParams.role;
      this.reason = this.getUnauthorizedReason();
    });
  }

  private getUnauthorizedReason(): string {
    switch (this.role) {
      case 'instructor':
        return 'You are not an instructor of any course.';
      case 'student':
        return 'You are not enrolled as a student in any course.';
      case 'admin':
        return 'You need to be an admin to access this page.';
      case 'maintainer':
        return 'You need to be a maintainer to access this page.';
      default:
        return 'You do not have the required role to access this page.';
    }
  }
}
