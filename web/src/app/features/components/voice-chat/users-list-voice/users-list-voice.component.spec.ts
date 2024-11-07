import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UsersListVoiceComponent } from './users-list-voice.component';
import {TextChannelQuery} from "../../../store/textChannel/text.channel.query";
import {ChannelService} from "../../../services/channel.service";
import {CommunityQuery} from "../../../store/community/community.query";
import {MatDialog} from "@angular/material/dialog";
import {UserService} from "../../../../core/services/user.service";

const channelServiceMock = {
  selectVoiceChannel: jest.fn()
};

const userServiceMock = {
  getPermission: jest.fn().mockReturnValue({
    canCreateChannel: true
  })
};

describe('UsersListVoiceComponent', () => {
  let component: UsersListVoiceComponent;
  let fixture: ComponentFixture<UsersListVoiceComponent>;

  let channelQuery: TextChannelQuery;
  let communityQuery: CommunityQuery;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UsersListVoiceComponent],
      providers: [
        MatDialog,
        {provide: ChannelService, useValue: channelServiceMock},
        {provide: UserService, useValue: userServiceMock}
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UsersListVoiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  beforeEach(() => {
    channelQuery = TestBed.inject(TextChannelQuery);
    communityQuery = TestBed.inject(CommunityQuery);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
