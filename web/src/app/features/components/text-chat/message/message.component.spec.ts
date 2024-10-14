import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MessageComponent } from './message.component';
import {MatDialog} from "@angular/material/dialog";
import {MessageService} from "../../../services/message.service";
import {Reaction} from "../../../models/reaction";
import {MessageAttachment} from "../../../models/message.attachment";

const messageServiceMock = {
  addReaction: jest.fn()
};

const messageMock = {
  id: '123',
  text: 'test message',
  channelId: '69',
  userId: '420',
  edited: false,
  updatedAt: new Date(),
  reactions: []
};

describe('MessageComponent', () => {
  let component: MessageComponent;
  let fixture: ComponentFixture<MessageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MessageComponent],
      providers: [
        MatDialog,
        {provide: MessageService, useValue: messageServiceMock}
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
