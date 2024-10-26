import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MessageComponent } from './message.component';
import {MatDialog} from "@angular/material/dialog";
import {MessageService} from "../../../services/message.service";
import {UserService} from "../../../../core/services/user.service";

const messageServiceMock = {
  addReaction: jest.fn()
};

const userServiceMock = {
  getUser: jest.fn(() =>  ({
    id: '123',
    username: 'test-username',
    imageUrl: 'test-location.jpg',
  }))
};

const messageMock = {
  id: '123',
  text: 'test message',
  channelId: '69',
  userId: '420',
  edited: false,
  updatedAt: new Date(),
  dateFormatted: '2024',
  reactions: [],
  attachments: []
};

describe('MessageComponent', () => {
  let component: MessageComponent;
  let fixture: ComponentFixture<MessageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MessageComponent],
      providers: [
        MatDialog,
        {provide: MessageService, useValue: messageServiceMock},
        {provide: UserService, useValue: userServiceMock}
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MessageComponent);
    component = fixture.componentInstance;
    component.message = messageMock;
    fixture.detectChanges();
  });

  beforeEach(() => {

  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
