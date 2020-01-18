import { Component, Input, OnInit } from '@angular/core';
import { ANONYMOUS_PREFIX } from '../../../../types/feedback-response-details';

/**
 * Button for instructor moderating responses.
 */
@Component({
  selector: 'tm-response-moderation-button',
  templateUrl: './response-moderation-button.component.html',
  styleUrls: ['./response-moderation-button.component.scss'],
})
export class ResponseModerationButtonComponent implements OnInit {

  @Input()
  session: any = {};

  @Input()
  relatedGiverEmail: string = '';

  @Input()
  moderatedQuestionId: string = '';

  constructor() { }

  ngOnInit(): void { }

  /**
   * Check if email starts with anonymous.
   */
  isAnonymousEmail(email: string): boolean {
    return email.startsWith(ANONYMOUS_PREFIX);
  }
}
