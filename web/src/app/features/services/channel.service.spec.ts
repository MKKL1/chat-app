import {TestBed} from '@angular/core/testing';

import {ChannelService} from './channel.service';
import {provideHttpClient} from "@angular/common/http";
import {HttpTestingController, provideHttpClientTesting} from "@angular/common/http/testing";
import {VoiceChannelStore} from "../store/voiceChannel/voice.channel.store";
import {TextChannelStore} from "../store/textChannel/text.channel.store";
import {Channel, ChannelType} from "../models/channel";
import {environment} from "../../../environment";
import {EventService} from "../../core/events/event.service";

let voiceChannel: Channel = {
  communityId: '123',
  id: '123',
  name: 'Test voice channel',
  type: ChannelType.Voice,
  overwrites: []
};

let textChannel: Channel = {
  communityId: '123',
  id: '124',
  name: 'Test text channel',
  type: ChannelType.Text,
  overwrites: []
};

const eventServiceMock = {
  on: jest.fn()
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
        provideHttpClientTesting(),
        VoiceChannelStore,
        TextChannelStore,
        {provide: EventService, useValue: eventServiceMock}
      ],
    });

    httpTesting = TestBed.inject(HttpTestingController);
    service = TestBed.inject(ChannelService);
    voiceChannelStore = TestBed.inject(VoiceChannelStore);
    textChannelStore = TestBed.inject(TextChannelStore);
  });

  afterEach(() => {
    httpTesting.verify();
    textChannelStore.reset();
    voiceChannelStore.reset();
  });

  test('should be created', () => {
    expect(service).toBeTruthy();
  });

  // test('should select voice chat', () => {
  //   service.selectVoiceChannel(voiceChannel);
  //   const state = voiceChannelStore.getValue();
  //   expect(state.active).toEqual(voiceChannel.id);
  // });
  //
  // test('should not select voice channel', () => {
  //   service.selectVoiceChannel(textChannel);
  //   const state = voiceChannelStore.getValue();
  //   expect(state.active).not.toBe(textChannel.id);
  // });
  //
  // test('should select text chat', () => {
  //   service.selectTextChannel(textChannel);
  //   const state = textChannelStore.getValue();
  //   expect(state.active).toEqual(textChannel.id);
  // });
  //
  // test('should not select text channel', () => {
  //   service.selectTextChannel(voiceChannel);
  //   const state = textChannelStore.getValue();
  //   expect(state.active).not.toBe(voiceChannel.id);
  // });
  //
  // test('should create new channel', () => {
  //   const mockNewChannel: Channel = {
  //     communityId: '123',
  //     id: '125',
  //     name: 'Created channel',
  //     type: ChannelType.Text,
  //     overwrites: []
  //   };
  //
  //   service.createChannel(mockNewChannel).subscribe((newChannel) => {
  //     expect(newChannel).toEqual(mockNewChannel);
  //
  //     const textChannelState = textChannelStore.getValue();
  //     const voiceChannelState = voiceChannelStore.getValue();
  //     expect(textChannelState.entities).toContain(mockNewChannel);
  //     expect(voiceChannelState.entities).toEqual([]);
  //   });
  //
  //   //const req = httpTesting.expectOne(`${environment.api}channels/${mockNewChannel.communityId}`);
  //   //expect(req.request.method).toBe('POST');
  //   //req.flush(mockNewChannel);
  // });

  // test('should edit channel', () => {
  //   const mockUpdatedChannel: Channel = {
  //     communityId: '123',
  //     id: '123',
  //     name: 'New name',
  //     type: ChannelType.Voice,
  //     overwrites: []
  //   };
  //
  //   voiceChannelStore.add(voiceChannel);
  //
  //   service.updateChannel(mockUpdatedChannel).subscribe((updatedChannel) => {
  //     expect(updatedChannel.name).toBe('New name');
  //
  //     const voiceStore = voiceChannelStore.getValue();
  //     expect(voiceStore.entities).toContain(updatedChannel);
  //     const textStore = textChannelStore.getValue();
  //     expect(textStore.entities).toEqual([]);
  //   });
  //
  //   const req = httpTesting.expectOne(`${environment.api}channels/${voiceChannel.id}`);
  //   expect(req.request.method).toBe('PUT');
  //   req.flush(mockUpdatedChannel);
  // });
  //
  // test('should delete channel', () => {
  //   textChannelStore.add(textChannel);
  //
  //   service.deleteChannel(textChannel.id!).subscribe(() => {
  //     const textStore = textChannelStore.getValue();
  //     expect(textStore.entities).not.toContain(textChannel);
  //   });
  //
  //   const req = httpTesting.expectOne(`${environment.api}channels/${textChannel.id}`);
  //   expect(req.request.method).toBe('DELETE');
  //   req.flush(null);
  // });
});
