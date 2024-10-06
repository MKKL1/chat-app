import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UsersListVoiceComponent } from './users-list-voice.component';
import {TextChannelQuery} from "../../../store/textChannel/text.channel.query";
import {ChannelService} from "../../../services/channel.service";
import {CommunityQuery} from "../../../store/community/community.query";
import {MatDialog} from "@angular/material/dialog";

const channelServiceMock = {
  selectVoiceChannel: jest.fn()
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
        {provide: ChannelService, useValue: channelServiceMock}
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
