import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { QuestionTextWithInfoModule } from '../../question-text-with-info/question-text-with-info.module';
import { SingleResponseModule } from '../single-response/single-response.module';
import { GroupedResponsesComponent } from './grouped-responses.component';

describe('GroupedResponsesComponent', () => {
  let component: GroupedResponsesComponent;
  let fixture: ComponentFixture<GroupedResponsesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [GroupedResponsesComponent],
      imports: [
        QuestionTextWithInfoModule,
        SingleResponseModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GroupedResponsesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
