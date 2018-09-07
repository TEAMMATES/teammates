import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NavigationService } from '../../navigation.service';

/**
 * About page.
 */
@Component({
  selector: 'tm-about-page',
  templateUrl: './about-page.component.html',
  styleUrls: ['./about-page.component.scss'],
})
export class AboutPageComponent implements OnInit {

  nDevelopers: number = 0;
  teamMembers: any[] = [];
  pastTeamMembers: any[] = [];
  committers: any[] = [];
  pastCommitters: any[] = [];
  majorContributors: any[] = [];
  multipleContributors: any[] = [];
  singleContributors: any[] = [];

  constructor(private router: Router, private navigationService: NavigationService,
      private httpClient: HttpClient) {}

  private setUrl(dev: any): any {
    if (dev.url) {
      return dev;
    }
    if (dev.username) {
      dev.url = `https://github.com/${dev.username}`;
    }
    return dev;
  }

  private setDisplayedName(dev: any): any {
    dev.displayedName = dev.name || `@${dev.username}`;
    return dev;
  }

  /**
   * Navigates user to another page.
   */
  navigateTo(url: string, event: any): void {
    this.navigationService.navigateTo(this.router, url, event);
  }

  ngOnInit(): void {
    this.httpClient.get('./assets/data/developers.json').subscribe((res: any) => {
      this.nDevelopers = res.teammembers.length + res.committers.length + res.contributors.length;
      this.teamMembers = res.teammembers.filter((n: any) => n.currentPosition).map(this.setUrl);
      this.pastTeamMembers = res.teammembers.filter((n: any) => !n.currentPosition).map(this.setUrl);
      this.committers = res.committers.filter((n: any) => !n.endPeriod).map(this.setUrl);
      this.pastCommitters = res.committers.filter((n: any) => n.endPeriod).map(this.setUrl);
      this.majorContributors = res.contributors.filter((n: any) => n.major).map(this.setUrl);
      this.multipleContributors = res.contributors.filter((n: any) => !n.major && n.multiple)
          .map(this.setUrl).map(this.setDisplayedName);
      this.singleContributors = res.contributors.filter((n: any) => !n.major && !n.multiple)
          .map(this.setUrl).map(this.setDisplayedName);
    });
  }

}
