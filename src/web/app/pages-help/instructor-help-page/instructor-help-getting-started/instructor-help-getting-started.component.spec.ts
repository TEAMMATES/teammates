import { DOCUMENT } from '@angular/common';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { DomSanitizer } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { PageScrollService, NGXPS_CONFIG } from 'ngx-page-scroll-core';
import { InstructorHelpGettingStartedComponent } from './instructor-help-getting-started.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('InstructorHelpGettingStartedComponent', () => {
  let component: InstructorHelpGettingStartedComponent;
  let fixture: ComponentFixture<InstructorHelpGettingStartedComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterModule.forRoot([]),
      ],
      providers: [
        {
          provide: DomSanitizer,
                   useValue: {
                     bypassSecurityTrustHtml: () => '',
                     sanitize: () => '',
                   },
        },
        { provide: DOCUMENT, useValue: document },
        PageScrollService,
        { provide: NGXPS_CONFIG, useValue: {} },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorHelpGettingStartedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
