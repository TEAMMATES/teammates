import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { NavigationService } from '../../navigation.service';

@Component({
  selector: 'tm-page-not-found',
  templateUrl: './page-not-found.component.html',
  styleUrls: ['./page-not-found.component.scss'],
})
export class PageNotFoundComponent {

  constructor(private router: Router, private navigationService: NavigationService) {}

  /**
   * Navigates user to another page.
   */
  public navigateTo(url: string, event: any): void {
    this.navigationService.navigateTo(this.router, url, event);
  }

}
