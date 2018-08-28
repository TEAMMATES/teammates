import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';

/**
 * About page.
 */
@Component({
  selector: 'tm-about-page',
  templateUrl: './about-page.component.html',
  styleUrls: ['./about-page.component.scss'],
})
export class AboutPageComponent implements OnInit {

  /**
   * Total number of contributors and developers.
   */
  nDevelopers: number = 0;

  /**
   * Current core team members.
   */
  teamMembers: any[] = [];

  /**
   * Past core team members.
   */
  pastTeamMembers: any[] = [];

  /**
   * Current committers.
   */
  committers: any[] = [];

  /**
   * Past committers.
   */
  pastCommitters: any[] = [];

  /**
   * Major contributors.
   */
  majorContributors: any[] = [];

  /**
   * Contributors who have had > 1 PR merged.
   */
  multipleContributors: any[] = [];

  /**
   * Contributors who have had just 1 PR merged.
   */
  singleContributors: any[] = [];

  constructor(private httpClient: HttpClient) {}

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
