import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StatusMessage } from './status-message';
import { StatusMessageComponent } from './status-message.component';

describe('StatusMessageComponent', () => {
  let component: StatusMessageComponent;
  let fixture: ComponentFixture<StatusMessageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [StatusMessageComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StatusMessageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  const messages: StatusMessage[] = [
    { message: 'a', color: 'black' },
    { message: 'b', color: 'red' },
    { message: 'c', color: 'green' },
  ];

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default view', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should show all three messages', () => {
    component.messages = messages;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should delete a message after clicking button', () => {
    component.messages = messages;
    fixture.detectChanges();

    const buttons: HTMLElement[] = fixture.nativeElement.querySelectorAll('div.alert button');

    buttons[0].click();
    fixture.detectChanges();

    const statusMessagesAfterClick: HTMLElement[] = fixture.nativeElement.querySelectorAll('div.alert div');
    const messageAfterDelete: StatusMessage[] = [
      { message: 'b', color: 'red' },
      { message: 'c', color: 'green' },
    ];

    expect(component.messages).toEqual(messageAfterDelete);
    expect(statusMessagesAfterClick.length).toEqual(2);
    expect(statusMessagesAfterClick[0].innerHTML).toEqual('b');
    expect(statusMessagesAfterClick[1].innerHTML).toEqual('c');

    buttons[1].click();
    buttons[2].click();
    fixture.detectChanges();

    const deleteAll: HTMLElement[] = fixture.nativeElement.querySelectorAll('div.alert div');
    const messageAfterDeleteAll: StatusMessage[] = [];

    expect(component.messages).toEqual(messageAfterDeleteAll);

    expect(deleteAll.length).toEqual(0);
  });

});
