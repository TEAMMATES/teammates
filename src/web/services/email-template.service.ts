import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpRequestService } from './http-request.service';
import { ResourceEndpoints } from '../types/api-const';
import { EmailTemplate, EmailTemplates } from '../types/api-output';
import { EmailTemplateUpdateRequest } from '../types/api-request';

/**
 * Handles email template related logic injection.
 */
@Injectable({
  providedIn: 'root',
})
export class EmailTemplateService {

  constructor(private httpRequestService: HttpRequestService) {}

  /**
   * Retrieves the list of configurable email template keys.
   */
  getEmailTemplates(): Observable<EmailTemplates> {
    return this.httpRequestService.get(ResourceEndpoints.EMAIL_TEMPLATES);
  }

  /**
   * Retrieves a single email template by key.
   * Returns the custom DB-backed template if one exists, or the static default otherwise.
   */
  getEmailTemplate(templateKey: string): Observable<EmailTemplate> {
    const paramMap: Record<string, string> = {
      templatekey: templateKey,
    };
    return this.httpRequestService.get(ResourceEndpoints.EMAIL_TEMPLATE, paramMap);
  }

  /**
   * Saves a custom email template, or resets it to the static default
   * when {@code request.resetToDefault} is {@code true}.
   */
  updateEmailTemplate(request: EmailTemplateUpdateRequest): Observable<EmailTemplate> {
    return this.httpRequestService.put(ResourceEndpoints.EMAIL_TEMPLATE, {}, request);
  }
}
