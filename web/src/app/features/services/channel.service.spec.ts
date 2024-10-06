import {TestBed} from '@angular/core/testing';

import {ChannelService} from './channel.service';
import {provideHttpClient} from "@angular/common/http";
import {HttpTestingController, provideHttpClientTesting} from "@angular/common/http/testing";
import {VoiceChannelStore} from "../store/voiceChannel/voice.channel.store";
import {TextChannelStore} from "../store/textChannel/text.channel.store";
import {Channel, ChannelType} from "../models/channel";
import {environment} from "../../../environment";

let voiceChannel: Channel = {
  communityId: "123", id: '123', name: "Test voice channel", type: ChannelType.Voice
};

let textChannel: Channel = {
  communityId: "123", id: '123', name: "Test text channel", type: ChannelType.Text
};

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
    textChannelStore.reset();
    voiceChannelStore.reset();
  });

  test('should be created', () => {
    expect(service).toBeTruthy();
  });

  // ideas ids should be unique
  test('should select voice chat', () => {
    service.selectVoiceChannel(voiceChannel);
    const state = voiceChannelStore.getValue();
    expect(state.active).toEqual(voiceChannel.id);
  });

  test('should not select voice channel', () => {
    service.selectVoiceChannel(textChannel);
    const state = voiceChannelStore.getValue();
    expect(state.active).not.toBe(textChannel.id);
  });

  test('should select text chat', () => {
    service.selectTextChannel(textChannel);
    const state = textChannelStore.getValue();
    expect(state.active).toEqual(textChannel.id);
  });

  test('should not select text channel', () => {
    service.selectVoiceChannel(voiceChannel);
    const state = textChannelStore.getValue();
    expect(state.active).not.toBe(voiceChannel.id);
  });


  test('should new create channel', () => {
    const mockNewChannel: Channel = {
      communityId: "123", id: "123", name: "Created channel", type: ChannelType.Text
    };

    service.createChannel(textChannel).subscribe(newChannel => {
      expect(newChannel).toEqual(mockNewChannel);

      const textChannelState = textChannelStore.getValue();
      const voiceChannelState = voiceChannelStore.getValue();
      // new channel should be added to textChannelStore because it is of type textChannel
      expect(textChannelState.entities).toEqual([textChannel]);
      expect(voiceChannelState.entities).toEqual([]);
    });

    const req = httpTesting.expectOne(environment.api + "channels");
    expect(req.request.method).toBe('POST');
    req.flush(mockNewChannel);
  });

  test('should edit channel', () => {
    let mockUpdatedChannel = {
      communityId: "123", id: '123', name: "New name", type: ChannelType.Voice
    };

    voiceChannelStore.add(voiceChannel);

    let voiceChannelToUpdate: Channel = {
      ...voiceChannel,
      name: "New name"
    };

    service.updateChannel(voiceChannelToUpdate).subscribe(updatedChannel => {
      expect(updatedChannel.name).toBe("New name");

      const voiceStore = voiceChannelStore.getValue();
      expect(voiceStore.entities).toBe([updatedChannel]);
      const textStore = textChannelStore.getValue();
      expect(textStore.entities).toBe([]);
    });

    const req = httpTesting.expectOne(environment.api + "channels/" + voiceChannel.id);
    expect(req.request.method).toBe('PUT');
    req.flush(mockUpdatedChannel);
  });

  test('should delete channel', () => {
    textChannelStore.add(textChannel);

    service.deleteChannel(textChannel.id!).subscribe(() => {
      const textStore = textChannelStore.getValue();
      expect(textStore.entities).toBe([]);
    });

    const req = httpTesting.expectOne(environment.api + "channels/" + textChannel.id);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
