import { Injectable } from '@angular/core';
import { TemplateSession, templateSessions } from './template-sessions';

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
