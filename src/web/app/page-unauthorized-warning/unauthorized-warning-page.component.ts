import { Component, inject } from '@angular/core';
import { NavigationService } from '../../services/navigation.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'tm-unauthorized-warning-page',
  styleUrl: './unauthorized-warning-page.component.css',
  templateUrl: './unauthorized-warning-page.component.html',
})
export class UnauthorizedWarningPageComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly navigationService = inject(NavigationService);

  entityType = '';

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.entityType = queryParams.entitytype;

      // Join course is protected by the auth guard only, so just read from resolver data.
      const authInfo = this.route.snapshot.data['authInfo'];
      if (!authInfo?.user) {
        this.navigationService.navigateWithErrorMessage('/web/front', 'You are not authorized to view this page.');
        return;
      }
    });
  }
}
