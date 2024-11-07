import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {VoiceChannelQuery} from "../store/voiceChannel/voice.channel.query";
import {environment} from "../../../environment";
import {LocalAudioTrack, LocalVideoTrack, Participant, ParticipantEvent, Room, RoomEvent} from "livekit-client";

@Injectable({
  providedIn: 'root'
})
export class VoiceChatService {

  token: string;
  wsUrl: string = 'ws://localhost:7880';

  room: Room | null = null;

  constructor(
    private http: HttpClient,
    private voiceChannelQuery: VoiceChannelQuery) { }

  async joinRoom(): Promise<void>{
    const channelId = this.voiceChannelQuery.getActiveId();
    this.http.get(environment.api + `channels/${channelId}/voice/join`)
      .subscribe(async (res: any) => {
        this.token = res.token;

        if (this.room !== null) {
          // TODO handle disconnect??
          this.room.disconnect()
            .then(this.room = null);
        }

        this.room = new Room();
        await this.room.connect(this.wsUrl, this.token);

        this.room.on(RoomEvent.ParticipantConnected, (participant) => {
          console.log(`User connected: ${participant.identity}`);
        });

        this.room.on(RoomEvent.ParticipantDisconnected, (participant) => {
          console.log(`User disconnected: ${participant.identity}`);
        });

        this.room.on(RoomEvent.ActiveSpeakersChanged, (speakers: Participant[]) => {
          speakers.forEach(s => console.log(s));
        });

        this.room.remoteParticipants.forEach(participant => {
          participant.on(ParticipantEvent.IsSpeakingChanged, (speaking: boolean) => {
            console.log(
              `${participant.identity} is ${speaking ? 'now' : 'no longer'} speaking. audio level: ${participant.audioLevel}`,
            );
          });
        })

        this.setCamera(true);
        this.setMicrophone(true);
      });
  }


  setCamera(allow: boolean){
    if(this.room){
      this.room.localParticipant.setCameraEnabled(allow);
    }
  }

  setMicrophone(allow: boolean){
    if(this.room){
      this.room.localParticipant.setMicrophoneEnabled(allow);
    }
  }

  setScreenSharing(allow: boolean){
    if(this.room){
      this.room.localParticipant.setScreenShareEnabled(allow);
    }
  }

  leaveRoom(): void {
    if (this.room) {
      this.room.disconnect();
    }
  }
}
