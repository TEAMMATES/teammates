import { Injectable } from '@angular/core';
import { FeedbackQuestion } from '../app/feedback-question';
import { default as templateSessions } from '../data/template-sessions.json';

/**
 * A template session.
 */
export interface TemplateSession {
  name: string;
  questions: FeedbackQuestion[];
}

/**
 * Handles sessions related logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class FeedbackSessionsService {

  constructor() { }

  /**
   * Gets template sessions.
   */
  getTemplateSessions(): TemplateSession[] {
    return templateSessions;
  }
}
