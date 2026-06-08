import { Component, OnInit } from '@angular/core';
import { default as developersData } from '../../../data/developers.json';
import { TeammatesRouterDirective } from '../../components/teammates-router/teammates-router.directive';

/**
 * Team member information from developers.json
 * Used for both current and past team members
 */
interface TeamMember {
  name: string;
  username?: string;
  currentPosition?: string;
  pastPositions?: string[];
  url?: string;
  avatarUrl?: string;
}

/**
 * Committer information from developers.json
 * Used for both current and past committers
 */
interface Committer {
  name: string;
  username: string;
  startPeriod: string;
  endPeriod?: string;
  url?: string;
  avatarUrl?: string;
}

/**
 * Contributor information from developers.json
 * Note: name and/or username may be optional in source data
 */
interface Contributor {
  name?: string;
  username?: string;
  major?: boolean;
  multiple?: boolean;
  url?: string;
  displayedName?: string;
}

/**
 * About page.
 */
@Component({
  selector: 'tm-about-page',
  templateUrl: './about-page.component.html',
  styleUrls: ['./about-page.component.scss'],
  imports: [TeammatesRouterDirective],
})
export class AboutPageComponent implements OnInit {
  nDevelopers = 0;
  teamMembers: TeamMember[] = [];
  pastTeamMembers: TeamMember[] = [];
  committers: Committer[] = [];
  pastCommitters: Committer[] = [];
  majorContributors: Contributor[] = [];
  multipleContributors: Contributor[] = [];
  singleContributors: Contributor[] = [];

  private setUrl<T extends { username?: string; url?: string; avatarUrl?: string }>(dev: T): T {
    if (dev.username) {
      if (!dev.url) {
        dev.url = `https://github.com/${dev.username}`;
      }
      dev.avatarUrl = `https://github.com/${dev.username}.png`;
    }
    return dev;
  }

  private setDisplayedName(dev: Contributor): Contributor {
    if (dev.name || dev.username) {
      dev.displayedName = dev.name || `@${dev.username}`;
    }
    return dev;
  }

  ngOnInit(): void {
    this.nDevelopers =
      developersData.teammembers.length + developersData.committers.length + developersData.contributors.length;
    this.teamMembers = developersData.teammembers.filter((n) => n.currentPosition).map((n) => this.setUrl(n));
    this.pastTeamMembers = developersData.teammembers.filter((n) => !n.currentPosition).map((n) => this.setUrl(n));
    this.committers = developersData.committers.filter((n) => !n.endPeriod).map((n) => this.setUrl(n));
    this.pastCommitters = developersData.committers.filter((n) => n.endPeriod).map((n) => this.setUrl(n));
    this.majorContributors = developersData.contributors.filter((n) => n.major).map((n) => this.setUrl(n));
    this.multipleContributors = developersData.contributors
      .filter((n) => !n.major && n.multiple)
      .map((n) => this.setUrl(n))
      .map((n) => this.setDisplayedName(n));
    this.singleContributors = developersData.contributors
      .filter((n) => !n.major && !n.multiple)
      .map((n) => this.setUrl(n))
      .map((n) => this.setDisplayedName(n));
  }
}
