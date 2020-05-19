import { Component, Input } from "@angular/core";
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxPageScrollCoreModule } from 'ngx-page-scroll-core';

import { TemplateSession } from '../../../../services/feedback-sessions.service';
import { Course } from '../../../../types/api-output';
import {
  SessionEditFormMode,
  SessionEditFormModel,
} from '../../../components/session-edit-form/session-edit-form-model';
import { InstructorHelpSessionsSectionComponent } from './instructor-help-sessions-section.component';
import { ExampleBoxComponent } from "../example-box/example-box.component";

@Component({ selector: 'tm-session-edit-form', template: '' })
class SessionEditFormStubComponent {
  @Input() formMode: SessionEditFormMode = SessionEditFormMode.ADD;
  @Input() model: SessionEditFormModel = {} as SessionEditFormModel;
  @Input() courseCandidates: Course[] = [];
  @Input() templateSessions: TemplateSession[] = [];
}

describe('InstructorHelpSessionsSectionComponent', () => {
  let component: InstructorHelpSessionsSectionComponent;
  let fixture: ComponentFixture<InstructorHelpSessionsSectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorHelpSessionsSectionComponent, ExampleBoxComponent, SessionEditFormStubComponent],
      imports: [NgbModule, RouterTestingModule, NgxPageScrollCoreModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorHelpSessionsSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
