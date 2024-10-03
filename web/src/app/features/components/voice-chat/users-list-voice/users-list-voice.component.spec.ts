import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UsersListVoiceComponent } from './users-list-voice.component';
import {TextChannelQuery} from "../../../store/textChannel/text.channel.query";
import {ChannelService} from "../../../services/channel.service";
import {CommunityQuery} from "../../../store/community/community.query";

describe('UsersListVoiceComponent', () => {
  let component: UsersListVoiceComponent;
  let fixture: ComponentFixture<UsersListVoiceComponent>;

  let channelQuery: TextChannelQuery;
  let communityQuery: CommunityQuery;
  let channelService: ChannelService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UsersListVoiceComponent]
    })
    .compileComponents();

    channelQuery = TestBed.inject(TextChannelQuery);
    communityQuery = TestBed.inject(CommunityQuery);
    channelService = TestBed.inject(ChannelService);

    fixture = TestBed.createComponent(UsersListVoiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
