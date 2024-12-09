import {Component, OnDestroy, OnInit, signal} from '@angular/core';
import {MatGridList, MatGridTile} from "@angular/material/grid-list";
import {UserPanelComponent} from "../../voice-chat/user-panel/user-panel.component";
import {UsersListVoiceComponent} from "../../voice-chat/users-list-voice/users-list-voice.component";
import {MatIcon} from "@angular/material/icon";
import {LayoutComponent} from "../../../../core/components/layout/layout.component";
import {ParticipantInfo, VoiceChatService} from "../../../services/voice-chat.service";
import {VoiceChannelQuery} from "../../../store/voiceChannel/voice.channel.query";
import {Channel} from "../../../models/channel";
import {TextChatComponent} from "../../text-chat/text-chat/text-chat.component";
import {Subscription} from "rxjs";
import {MemberQuery} from "../../../store/member/member.query";
import {CommunityQuery} from "../../../store/community/community.query";
import {Member} from "../../../models/member";
import {NgClass} from "@angular/common";
import {MessageService} from "primeng/api";
import {MatButtonModule} from "@angular/material/button";
import {VoiceChannelStore} from "../../../store/voiceChannel/voice.channel.store";

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
    TextChatComponent,
    NgClass,
    MatButtonModule
  ],
  templateUrl: './voice-channel.component.html',
  styleUrl: './voice-channel.component.scss'
})


export class VoiceChannelComponent implements OnInit, OnDestroy{
  selectedChannel = signal<Channel | null>(null);

  participants = signal<ParticipantInfo[]>([]);
  speakers = signal<string[]>([]);
  // all community members
  members = signal<Member[]>([]);

  clientMicrophone = signal<boolean>(false);
  clientCamera = signal<boolean>(false);
  sharingScreen = signal<boolean>(false);

  querySubscription: Subscription;
  participantsSubscription: Subscription;
  speakersSubscription: Subscription;
  memberSubscription: Subscription;

  constructor(
    private voiceChat: VoiceChatService,
    private voiceChannelQuery: VoiceChannelQuery,
    private voiceChannelStore: VoiceChannelStore,
    private memberQuery: MemberQuery,
    private communityQuery: CommunityQuery,
    private messageService: MessageService) {
  }

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
        this.participants.set(participants);
    });

    this.speakersSubscription = this.voiceChat.speakersSubject$
      .subscribe(speakers => {
        this.speakers.set(speakers);
    });
  }

  toggleClientMicrophone(){
    this.clientMicrophone.set(!this.clientMicrophone());
    this.voiceChat.setMicrophone(this.clientMicrophone());
  }

  toggleClientCamera(){
    this.clientCamera.set(!this.clientCamera());
    this.voiceChat.setCamera(this.clientCamera());
  }

  shareScreen(){
    this.sharingScreen.set(!this.sharingScreen());
    this.voiceChat.setScreenSharing(this.sharingScreen());
  }

  disconnect(){
    this.voiceChat.leaveRoom();
    this.voiceChannelStore.removeActive(this.selectedChannel()?.id);
    this.selectedChannel.set(null);
    this.clientCamera.set(false);
    this.clientMicrophone.set(false);
    this.messageService.add({severity: 'info', summary: 'Disconnected from channel'});
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
