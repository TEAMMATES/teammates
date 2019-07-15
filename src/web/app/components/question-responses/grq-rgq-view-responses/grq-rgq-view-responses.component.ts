import { Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import {
  InstructorSessionResultSectionType,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';

/**
 * Component to display list of responses in GRQ/RGQ view.
 */
@Component({
  selector: 'tm-grq-rgq-view-responses',
  templateUrl: './grq-rgq-view-responses.component.html',
  styleUrls: ['./grq-rgq-view-responses.component.scss'],
})
export class GrqRgqViewResponsesComponent implements OnInit, OnChanges {

  @Input() responses: any = {};
  @Input() section: string = '';
  @Input() sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;
  @Input() groupByTeam: boolean = true;
  @Input() showStatistics: boolean = true;
  @Input() indicateMissingResponses: boolean = true;
  @Input() timeZone: string = '';

  @Input() isGrq: boolean = true;

  @Output() commentsChangeInResponse: EventEmitter<any> = new EventEmitter();

  teamsToUsers: { [key: string]: string[] } = {};
  teamExpanded: { [key: string]: boolean } = {};
  userToTeamName: { [key: string]: any } = {};
  responsesToShow: { [key: string]: { [key: string]: any[] } } = {};

  constructor() { }

  ngOnInit(): void {
    this.filterResponses();
  }

  ngOnChanges(): void {
    this.filterResponses();
  }

  private filterResponses(): void {
    this.responsesToShow = {};
    this.teamsToUsers = {};
    this.teamExpanded = {};
    this.userToTeamName = {};
    for (const question of this.responses) {
      for (const response of question.allResponses) {
        if (this.isGrq) {
          this.teamsToUsers[response.giverTeam] = this.teamsToUsers[response.giverTeam] || [];
          if (this.teamsToUsers[response.giverTeam].indexOf(response.giver) === -1) {
            this.teamsToUsers[response.giverTeam].push(response.giver);
            this.teamExpanded[response.giverTeam] = false;
          }
          this.userToTeamName[response.giver] = {
            teamName: response.giverTeam,
            isExpanded: false,
          };
        } else {
          if (!response.recipientTeam) {
            // Recipient is team
            if (this.teamsToUsers[response.recipient].indexOf(response.recipient) === -1) {
              this.teamsToUsers[response.recipient].push(response.recipient);
              this.teamExpanded[response.recipient] = false;
            }
            this.userToTeamName[response.recipient] = {
              teamName: response.recipient,
              isExpanded: false,
            };
            continue;
          }
          this.teamsToUsers[response.recipientTeam] = this.teamsToUsers[response.recipientTeam] || [];
          if (this.teamsToUsers[response.recipientTeam].indexOf(response.recipient) === -1) {
            this.teamsToUsers[response.recipientTeam].push(response.recipient);
            this.teamExpanded[response.recipientTeam] = false;
          }
          this.userToTeamName[response.recipient] = {
            teamName: response.recipientTeam,
            isExpanded: false,
          };
        }
      }
    }

    for (const user of Object.keys(this.userToTeamName)) {
      for (const question of this.responses) {
        const questionCopy: any = JSON.parse(JSON.stringify(question));
        questionCopy.allResponses = questionCopy.allResponses.filter((response: any) => {
          if (this.isGrq && user !== response.giver) {
            return false;
          }
          if (!this.isGrq && user !== response.recipient) {
            return false;
          }

          let shouldDisplayBasedOnSection: boolean = true;
          if (this.section) {
            switch (this.sectionType) {
              case InstructorSessionResultSectionType.EITHER:
                shouldDisplayBasedOnSection =
                    response.giverSection === this.section || response.recipientSection === this.section;
                break;
              case InstructorSessionResultSectionType.GIVER:
                shouldDisplayBasedOnSection = response.giverSection === this.section;
                break;
              case InstructorSessionResultSectionType.EVALUEE:
                shouldDisplayBasedOnSection = response.recipientSection === this.section;
                break;
              case InstructorSessionResultSectionType.BOTH:
                shouldDisplayBasedOnSection =
                    response.giverSection === this.section && response.recipientSection === this.section;
                break;
              default:
            }
          }
          if (!shouldDisplayBasedOnSection) {
            return false;
          }

          return true;
        });
        if (questionCopy.allResponses.length) {
          const others: any[] = questionCopy.allResponses.map((response: any) => {
            return this.isGrq ? response.recipient : response.giver;
          });
          for (const other of others) {
            const questionCopy2: any = JSON.parse(JSON.stringify(questionCopy));
            questionCopy2.allResponses = questionCopy2.allResponses.filter((response: any) => {
              return this.isGrq ? response.recipient === other : response.giver === other;
            });
            this.responsesToShow[user] = this.responsesToShow[user] || {};
            this.responsesToShow[user][other] = this.responsesToShow[user][other] || [];
            this.responsesToShow[user][other].push(questionCopy2);
          }
        }
      }
    }
  }

  /**
   * Triggers the commentsChangeInResponse event.
   */
  triggerCommentsChangeInResponseEvent(response: any): void {
    this.commentsChangeInResponse.emit(response);
  }
}
