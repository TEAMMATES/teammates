import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { of, throwError } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import { AdminEmailTemplatesPageComponent } from './admin-email-templates-page.component';
import { EmailTemplateService } from '../../../services/email-template.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { EmailTemplate, EmailTemplates } from '../../../types/api-output';

const testCustomTemplate: EmailTemplate = {
  templateKey: 'NEW_INSTRUCTOR_ACCOUNT_WELCOME',
  subject: 'Custom Welcome Subject',
  body: '<p>Please visit <a href="${joinUrl}">${joinUrl}</a> to join.</p>',
  isCustomized: true,
  updatedAt: new Date('2026-01-01T00:00:00Z').getTime(),
};

const testDefaultTemplate: EmailTemplate = {
  templateKey: 'NEW_INSTRUCTOR_ACCOUNT_WELCOME',
  subject: 'TEAMMATES: Welcome to TEAMMATES! ${userName}',
  body: '<p>Hello ${userName}, please join via <a href="${joinUrl}">${joinUrl}</a></p>',
  isCustomized: false,
};

const testTemplateList: EmailTemplates = {
  templateKeys: ['NEW_INSTRUCTOR_ACCOUNT_WELCOME'],
};

describe('AdminEmailTemplatesPageComponent', () => {
  let component: AdminEmailTemplatesPageComponent;
  let fixture: ComponentFixture<AdminEmailTemplatesPageComponent>;
  let emailTemplateService: EmailTemplateService;
  let statusMessageService: StatusMessageService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
      ],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminEmailTemplatesPageComponent);
    emailTemplateService = TestBed.inject(EmailTemplateService);
    statusMessageService = TestBed.inject(StatusMessageService);
    component = fixture.componentInstance;
    jest.spyOn(emailTemplateService, 'getEmailTemplates').mockReturnValue(of(testTemplateList));
    jest.spyOn(emailTemplateService, 'getEmailTemplate').mockReturnValue(of(testDefaultTemplate));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default view', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when template keys are loading', () => {
    component.isKeysLoading = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when template keys failed to load', () => {
    component.templateKeys = [];
    component.hasKeysLoadingFailed = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when template body is loading', () => {
    component.isTemplateLoading = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should populate template keys and auto-load first template on init', () => {
    expect(component.templateKeys).toEqual(['NEW_INSTRUCTOR_ACCOUNT_WELCOME']);
    expect(component.selectedKey).toEqual('NEW_INSTRUCTOR_ACCOUNT_WELCOME');
    expect(component.model.subject).toEqual(testDefaultTemplate.subject);
    expect(component.model.body).toEqual(testDefaultTemplate.body);
    expect(component.model.isCustomized).toBeFalsy();
  });

  it('should set hasKeysLoadingFailed and show error toast when template list fails to load', () => {
    component.hasKeysLoadingFailed = false;
    jest.spyOn(emailTemplateService, 'getEmailTemplates').mockReturnValue(throwError(() => ({
      error: { message: 'Failed to fetch templates.' },
    })));
    const spy: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('Failed to fetch templates.');
      });

    component.loadTemplateKeys();

    expect(spy).toHaveBeenCalledTimes(1);
    expect(component.hasKeysLoadingFailed).toBeTruthy();
  });

  it('should load custom template into model when getEmailTemplate returns isCustomized=true', () => {
    jest.spyOn(emailTemplateService, 'getEmailTemplate').mockReturnValue(of(testCustomTemplate));

    component.loadTemplate('NEW_INSTRUCTOR_ACCOUNT_WELCOME');

    expect(component.model.subject).toEqual(testCustomTemplate.subject);
    expect(component.model.body).toEqual(testCustomTemplate.body);
    expect(component.model.isCustomized).toBeTruthy();
  });

  it('should load default template into model when getEmailTemplate returns isCustomized=false', () => {
    jest.spyOn(emailTemplateService, 'getEmailTemplate').mockReturnValue(of(testDefaultTemplate));

    component.loadTemplate('NEW_INSTRUCTOR_ACCOUNT_WELCOME');

    expect(component.model.subject).toEqual(testDefaultTemplate.subject);
    expect(component.model.body).toEqual(testDefaultTemplate.body);
    expect(component.model.isCustomized).toBeFalsy();
  });

  it('should show error toast and clear loading flag when loadTemplate fails', () => {
    jest.spyOn(emailTemplateService, 'getEmailTemplate').mockReturnValue(throwError(() => ({
      error: { message: 'Template not found.' },
    })));
    const spy: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('Template not found.');
      });

    component.loadTemplate('NEW_INSTRUCTOR_ACCOUNT_WELCOME');

    expect(spy).toHaveBeenCalledTimes(1);
    expect(component.isTemplateLoading).toBeFalsy();
  });

  it('should update model.isCustomized and show success toast on successful save', () => {
    jest.spyOn(emailTemplateService, 'updateEmailTemplate').mockReturnValue(of(testCustomTemplate));
    const spy: SpyInstance = jest.spyOn(statusMessageService, 'showSuccessToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('Email template saved successfully.');
      });
    component.model.subject = 'Modified Subject';
    component.model.body = testCustomTemplate.body;

    component.saveTemplate();

    expect(spy).toHaveBeenCalledTimes(1);
    expect(component.model.isCustomized).toBeTruthy();
    expect(component.isSaving).toBeFalsy();
  });

  it('should show warning toast and not call updateEmailTemplate when saveTemplate is called with no changes', () => {
    const updateSpy: SpyInstance = jest.spyOn(emailTemplateService, 'updateEmailTemplate');
    const warnSpy: SpyInstance = jest.spyOn(statusMessageService, 'showWarningToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('No changes made to save.');
      });

    component.saveTemplate();

    expect(warnSpy).toHaveBeenCalledTimes(1);
    expect(updateSpy).not.toHaveBeenCalled();
  });

  it('should show error toast with exact backend message when save fails due to missing placeholder (400)', () => {
    const placeholderErrorMessage = 'Email body is missing required placeholder(s): ${joinUrl}';
    jest.spyOn(emailTemplateService, 'updateEmailTemplate').mockReturnValue(throwError(() => ({
      error: { message: placeholderErrorMessage },
    })));
    const spy: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual(placeholderErrorMessage);
      });
    component.model.subject = 'Modified Subject';

    component.saveTemplate();

    expect(spy).toHaveBeenCalledTimes(1);
    expect(component.isSaving).toBeFalsy();
  });

  it('should reset model to defaults and show success toast on successful revert', () => {
    component.model = {
      subject: testCustomTemplate.subject,
      body: testCustomTemplate.body,
      isCustomized: true,
    };
    jest.spyOn(emailTemplateService, 'updateEmailTemplate').mockReturnValue(of(testDefaultTemplate));
    const spy: SpyInstance = jest.spyOn(statusMessageService, 'showSuccessToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('Email template reverted to default.');
      });

    component.revertToDefault();

    expect(spy).toHaveBeenCalledTimes(1);
    expect(component.model.subject).toEqual(testDefaultTemplate.subject);
    expect(component.model.body).toEqual(testDefaultTemplate.body);
    expect(component.model.isCustomized).toBeFalsy();
    expect(component.isReverting).toBeFalsy();
  });

  it('should show error toast and clear isReverting flag when revert fails', () => {
    jest.spyOn(emailTemplateService, 'updateEmailTemplate').mockReturnValue(throwError(() => ({
      error: { message: 'Revert failed.' },
    })));
    const spy: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('Revert failed.');
      });

    component.revertToDefault();

    expect(spy).toHaveBeenCalledTimes(1);
    expect(component.isReverting).toBeFalsy();
  });

  it('should call loadTemplate with the new selectedKey when template selection changes', () => {
    const loadTemplateSpy: SpyInstance = jest.spyOn(component, 'loadTemplate');
    component.selectedKey = 'NEW_INSTRUCTOR_ACCOUNT_WELCOME';
    component.onTemplateKeyChange();
    expect(loadTemplateSpy).toHaveBeenCalledWith('NEW_INSTRUCTOR_ACCOUNT_WELCOME');
  });
});
