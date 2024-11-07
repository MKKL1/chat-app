import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TextChatsListComponent } from './text-chats-list.component';
import {TextChannelQuery} from "../../../store/textChannel/text.channel.query";
import {CommunityQuery} from "../../../store/community/community.query";
import {MatDialog} from "@angular/material/dialog";
import {ChannelService} from "../../../services/channel.service";
import {UserService} from "../../../../core/services/user.service";

const channelServiceMock = {
  selectTextChannel: jest.fn()
};

const userServiceMock = {
  getPermission: jest.fn().mockReturnValue({
    canCreateChannel: true
  })
};

describe('TextChatsListComponent', () => {
  let component: TextChatsListComponent;
  let fixture: ComponentFixture<TextChatsListComponent>;

  let channelQuery: TextChannelQuery;
  let communityQuery: CommunityQuery;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TextChatsListComponent],
      providers: [
        MatDialog,
        {provide: ChannelService, useValue: channelServiceMock},
        {provide: UserService, useValue: userServiceMock}
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TextChatsListComponent);
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
