import { TestBed } from '@angular/core/testing';

import { MessageService } from './message.service';
import {provideHttpClient} from "@angular/common/http";
import {HttpTestingController, provideHttpClientTesting} from "@angular/common/http/testing";
import {MessageStore} from "../store/message/message.store";
import {TextChannelQuery} from "../store/textChannel/text.channel.query";

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
        provideHttpClientTesting()
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

  it('should get messages', () => {
    expect().nothing();
  });

  it('should send message', () => {
    expect().nothing();
  });

  it('should edit message', () => {
    expect().nothing();
  });

  it('should delete message', () => {
    expect().nothing();
  });
});
