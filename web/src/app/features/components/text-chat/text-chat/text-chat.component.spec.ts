import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TextChatComponent } from './text-chat.component';
import {MessageService} from "../../../services/message.service";
import {MessageQuery} from "../../../store/message/message.query";
import {TextChannelQuery} from "../../../store/textChannel/text.channel.query";
import {NoopAnimationsModule} from "@angular/platform-browser/animations";

const messageServiceMock = {
  getMessages: jest.fn()
};

describe('TextChatComponent', () => {
  let component: TextChatComponent;
  let fixture: ComponentFixture<TextChatComponent>;

  let messageQuery: MessageQuery;
  let channelQuery: TextChannelQuery;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TextChatComponent, NoopAnimationsModule],
      providers: [
        {provide: MessageService, useValue: messageServiceMock}
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TextChatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  beforeEach(() => {
    messageQuery = TestBed.inject(MessageQuery);
    channelQuery = TestBed.inject(TextChannelQuery);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
