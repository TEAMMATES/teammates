import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { EmailTemplateService } from './email-template.service';
import { HttpRequestService } from './http-request.service';
import createSpyFromClass from '../test-helpers/create-spy-from-class';
import { ResourceEndpoints } from '../types/api-const';
import { EmailTemplateUpdateRequest } from '../types/api-request';

const validUpdateRequest: EmailTemplateUpdateRequest = {
  templateKey: 'NEW_INSTRUCTOR_ACCOUNT_WELCOME',
  subject: 'Custom Subject',
  // eslint-disable-next-line no-template-curly-in-string
  body: '<p>Please visit <a href="${joinUrl}">${joinUrl}</a> to join.</p>',
  resetToDefault: false,
};

const resetRequest: EmailTemplateUpdateRequest = {
  templateKey: 'NEW_INSTRUCTOR_ACCOUNT_WELCOME',
  subject: '',
  body: '',
  resetToDefault: true,
};

describe('EmailTemplateService', () => {
  let spyHttpRequestService: any;
  let service: EmailTemplateService;

  beforeEach(() => {
    spyHttpRequestService = createSpyFromClass(HttpRequestService);
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(EmailTemplateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute GET when retrieving template list', () => {
    service.getEmailTemplates();
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(
      ResourceEndpoints.EMAIL_TEMPLATES,
    );
  });

  it('should execute GET with templatekey param when retrieving single template', () => {
    const paramMap: Record<string, string> = {
      templatekey: 'NEW_INSTRUCTOR_ACCOUNT_WELCOME',
    };
    service.getEmailTemplate('NEW_INSTRUCTOR_ACCOUNT_WELCOME');
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(
      ResourceEndpoints.EMAIL_TEMPLATE,
      paramMap,
    );
  });

  it('should execute PUT when saving a custom template', () => {
    service.updateEmailTemplate(validUpdateRequest);
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(
      ResourceEndpoints.EMAIL_TEMPLATE,
      {},
      validUpdateRequest,
    );
  });

  it('should execute PUT with resetToDefault=true when reverting to default', () => {
    service.updateEmailTemplate(resetRequest);
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(
      ResourceEndpoints.EMAIL_TEMPLATE,
      {},
      resetRequest,
    );
  });
});
