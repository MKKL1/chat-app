import { TestBed } from '@angular/core/testing';

import { ChannelService } from './channel.service';
import {provideHttpClient} from "@angular/common/http";
import {HttpTestingController, provideHttpClientTesting} from "@angular/common/http/testing";
import {VoiceChannelStore} from "../store/voiceChannel/voice.channel.store";
import {TextChannelStore} from "../store/textChannel/text.channel.store";
import {Channel} from "../models/channel";

export class MockChannelService{
  selectVoiceChannel(channel: Channel){

  }
}

describe('ChannelService', () => {
  let service: ChannelService;
  let voiceChannelStore: VoiceChannelStore;
  let textChannelStore: TextChannelStore;
  let httpTesting: HttpTestingController;

  beforeEach(() => {

    TestBed.configureTestingModule({
      providers: [
        ChannelService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    httpTesting = TestBed.inject(HttpTestingController);

    service = TestBed.inject(ChannelService);
    voiceChannelStore = TestBed.inject(VoiceChannelStore);
    textChannelStore = TestBed.inject(TextChannelStore);
  });

  afterEach(()=> {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should select voice chat', () => {

  });

  it('should select text chat', () => {

  });

  it('should create channel', () => {

  });

  it('should edit channel', () => {

  });

  it('should delete channel', () => {

  });
});
