import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MessageInputComponent } from './message-input.component';
import {MessageService} from "../../../services/message.service";
import {NoopAnimationsModule} from "@angular/platform-browser/animations";

const messageServiceMock = {
  sendMessage: jest.fn()
};

// lot of test here to do :))

describe('MessageInputComponent', () => {
  let component: MessageInputComponent;
  let fixture: ComponentFixture<MessageInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MessageInputComponent, NoopAnimationsModule],
      providers: [
        {provide: MessageService, useValue: messageServiceMock}
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MessageInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
