import { TestBed } from '@angular/core/testing';

import { CommunityService } from './community.service';
import {provideHttpClient} from "@angular/common/http";
import {HttpTestingController, provideHttpClientTesting} from "@angular/common/http/testing";
import {CommunityStore} from "../store/community/community.store";
import {CommunityQuery} from "../store/community/community.query";
import {VoiceChannelQuery} from "../store/voiceChannel/voice.channel.query";
import {VoiceChannelStore} from "../store/voiceChannel/voice.channel.store";
import {TextChannelQuery} from "../store/textChannel/text.channel.query";
import {TextChannelStore} from "../store/textChannel/text.channel.store";
import {MemberStore} from "../store/member/member.store";
import {RoleStore} from "../store/role/role.store";
import {EventService} from "../../core/events/event.service";

const eventServiceMock = {
  handleNewRequestStream: jest.fn()
};

describe('CommunityService', () => {
  let service: CommunityService;
  let httpTesting: HttpTestingController;
  let communityStore: CommunityStore;
  let communityQuery: CommunityQuery;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        CommunityService,
        provideHttpClient(),
        provideHttpClientTesting(),
        {provide: EventService, useValue: eventServiceMock}
      ]
    });

    httpTesting = TestBed.inject(HttpTestingController);

    service = TestBed.inject(CommunityService);
    communityStore = TestBed.inject(CommunityStore);
    communityQuery = TestBed.inject(CommunityQuery);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch community', () => {
    const voiceChannelQuery = TestBed.inject(VoiceChannelQuery);
    const voiceChannelStore = TestBed.inject(VoiceChannelStore);
    const textChannelQuery = TestBed.inject(TextChannelQuery);
    const textChannelStore = TestBed.inject(TextChannelStore);
    const memberStore = TestBed.inject(MemberStore);
    const roleStore = TestBed.inject(RoleStore);


  });

  it('should get communities', () => {

  });

  it('should create community', () => {

  });

  it('should edit community', () => {

  });

  it('should delete community', () => {

  });

  it('should create invitation', () => {

  });

  it('should accept invitation', () => {

  });
});
