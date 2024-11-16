import { TestBed } from '@angular/core/testing';

import { MessageService } from './message.service';
import {provideHttpClient} from "@angular/common/http";
import {HttpTestingController, provideHttpClientTesting} from "@angular/common/http/testing";
import {MessageStore} from "../store/message/message.store";
import {TextChannelQuery} from "../store/textChannel/text.channel.query";
import {EventService} from "../../core/events/event.service";
import {UserService} from "../../core/services/user.service";

const eventServiceMock = {
  on: jest.fn()
};

const userServiceMock = {
  getUser: jest.fn().mockReturnValue({
    id: "123"
  })

};

describe('MessageService', () => {
  let service: MessageService;
  let messageStore: MessageStore;
  let channelQuery: TextChannelQuery;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        MessageService,
        provideHttpClient(),
        provideHttpClientTesting(),
        {provide: EventService, useValue: eventServiceMock},
        {provide: UserService, useValue: userServiceMock}
      ]
    });

    httpTesting = TestBed.inject(HttpTestingController);
    service = TestBed.inject(MessageService);
    messageStore = TestBed.inject(MessageStore);
    channelQuery = TestBed.inject(TextChannelQuery);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // it('should get messages', () => {
  //
  // });
  //
  // it('should send message', () => {
  //
  // });
  //
  // it('should edit message', () => {
  //
  // });
  //
  // it('should delete message', () => {
  //
  // });
});
