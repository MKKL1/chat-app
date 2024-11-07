import {Component, OnDestroy, OnInit, signal} from '@angular/core';
import {MatGridList, MatGridTile} from "@angular/material/grid-list";
import {UserPanelComponent} from "../../voice-chat/user-panel/user-panel.component";
import {UsersListVoiceComponent} from "../../voice-chat/users-list-voice/users-list-voice.component";
import {MatIcon} from "@angular/material/icon";
import {LayoutComponent} from "../../../../core/components/layout/layout.component";
import {VoiceChatService} from "../../../services/voice-chat.service";
import {VoiceChannelQuery} from "../../../store/voiceChannel/voice.channel.query";
import {UserService} from "../../../../core/services/user.service";
import {Channel} from "../../../models/channel";
import {TextChatComponent} from "../../text-chat/text-chat/text-chat.component";
import {Participant} from "livekit-client";
import {Subscription} from "rxjs";
import {MemberQuery} from "../../../store/member/member.query";
import {CommunityQuery} from "../../../store/community/community.query";
import {Member} from "../../../models/member";

@Component({
  selector: 'app-voice-channel',
  standalone: true,
  imports: [
    MatGridList,
    MatGridTile,
    UserPanelComponent,
    UsersListVoiceComponent,
    MatIcon,
    LayoutComponent,
    TextChatComponent
  ],
  templateUrl: './voice-channel.component.html',
  styleUrl: './voice-channel.component.scss'
})


export class VoiceChannelComponent implements OnInit, OnDestroy{
  selectedChannel = signal<Channel | null>(null);

  participants = signal<string[]>([]);
  // all community members
  members = signal<Member[]>([]);

  clientMuted: boolean = false;
  clientCamera: boolean = false;

  querySubscription: Subscription;
  participantsSubscription: Subscription;
  speakersSubscription: Subscription;
  memberSubscription: Subscription;

  constructor(
    private voiceChat: VoiceChatService,
    private voiceChannelQuery: VoiceChannelQuery,
    private memberQuery: MemberQuery,
    private communityQuery: CommunityQuery,
    private userService: UserService) {
  }

  // what happens if i changed community during talking?

  ngOnInit() {
    this.memberSubscription = this.memberQuery.selectAll({
      filterBy: entity => entity.communityId === this.communityQuery.getActiveId()
    }).subscribe(members => {
      this.members.set(members);
    });

    this.querySubscription = this.voiceChannelQuery
      .selectActive()
      .subscribe(channel => {
        this.selectedChannel.set(channel!);
    });

    this.participantsSubscription = this.voiceChat.participantsSubject$
      .subscribe(participants => {
        console.log(participants);
        this.participants.set(participants);
    });

    this.speakersSubscription = this.voiceChat.speakersSubject$
      .subscribe(speakers => {
        console.log(speakers);
    });
  }

  toggleClientMuted(){
    this.clientMuted = !this.clientMuted;
    this.voiceChat.setMicrophone(this.clientMuted);
  }

  toggleClientCamera(){
    this.clientCamera = !this.clientCamera;
    this.voiceChat.setCamera(this.clientCamera);
  }

  disconnect(){
    this.voiceChat.leaveRoom();
  }

  findParticipantData(participantId: string): Member{
    return this.members().filter(member => member.id === participantId)[0];
  }

  ngOnDestroy() {
    this.querySubscription.unsubscribe();
    this.participantsSubscription.unsubscribe();
    this.speakersSubscription.unsubscribe();
    this.memberSubscription.unsubscribe();
  }
}
