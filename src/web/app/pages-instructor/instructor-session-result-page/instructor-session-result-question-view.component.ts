import { Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';

import { FormatPhotoUrlPipe } from '../../components/teammates-common/format-photo-url.pipe';
import { QuestionTabModel } from './instructor-session-result-page.component';
import { InstructorSessionResultView } from './instructor-session-result-view';
import { InstructorSessionResultViewType } from './instructor-session-result-view-type.enum';

/**
 * Instructor sessions results page question view.
 */
@Component({
  selector: 'tm-instructor-session-result-question-view',
  templateUrl: './instructor-session-result-question-view.component.html',
  styleUrls: ['./instructor-session-result-question-view.component.scss'],
})
export class InstructorSessionResultQuestionViewComponent
    extends InstructorSessionResultView implements OnInit, OnChanges {

  @Output()
  loadQuestion: EventEmitter<string> = new EventEmitter();

  @Input() questions: Record<string, QuestionTabModel> = {};

  questionsOrder: QuestionTabModel[] = [];
  userToPhotoUrl: Record<string, string> = {};
  userToEmail: Record<string, string> = {};

  constructor() {
    super(InstructorSessionResultViewType.QUESTION);
  }

  ngOnInit(): void {
    this.sortQuestion();
  }

  ngOnChanges(): void {
    this.sortQuestion();
  }

  loadPhotoHandler(user: string): void {
    if (!this.userToEmail[user]) {
      let continueFinding: boolean = true;
      for (const question of this.questionsOrder) {
        for (const response of question.responses) {
          if (response.giver === user && response.giverEmail) {
            this.userToEmail[user] = response.giverEmail;
            continueFinding = false;
            break;
          }
          if (response.recipient === user && response.recipientEmail) {
            this.userToEmail[user] = response.recipientEmail;
            continueFinding = false;
            break;
          }
        }

        if (!continueFinding) {
          break;
        }
      }
    }

    this.userToPhotoUrl[user] = new FormatPhotoUrlPipe().transform(this.session.courseId, this.userToEmail[user]);
  }

  sortQuestion(): void {
    this.questionsOrder = Object.values(this.questions)
        .sort((val1: QuestionTabModel, val2: QuestionTabModel) => {
          return val1.question.questionNumber - (val2.question.questionNumber);
        });
  }
}
