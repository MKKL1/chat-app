import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {VoiceChannelQuery} from "../store/voiceChannel/voice.channel.query";
import {environment} from "../../../environment";
import {
  Participant,
  ParticipantEvent,
  Room,
  RoomEvent,
} from "livekit-client";
import {BehaviorSubject} from "rxjs";

export interface ParticipantInfo {
  identity: string;
  audioEnabled: boolean;
  videoEnabled: boolean;
  screenShareEnabled: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class VoiceChatService{

  token: string;
  wsUrl: string = environment.livekitUrl;

  room: Room | null = null;

  audio: HTMLMediaElement;

  participantsSubject$ = new BehaviorSubject<ParticipantInfo[]>([]);
  speakersSubject$ = new BehaviorSubject<string[]>([]);

  constructor(
    private http: HttpClient,
    private voiceChannelQuery: VoiceChannelQuery) {

    this.audio = document.getElementById('audio-player') as HTMLMediaElement;
  }

  async joinRoom(): Promise<void>{
    if (this.room !== null) {
      this.leaveRoom();
    }

    const channelId = this.voiceChannelQuery.getActiveId();
    this.http.get(environment.api + `channels/${channelId}/voice/join`)
      .subscribe(async (res: any) => {
        this.token = res.token;
        this.room = new Room();
        await this.room.connect(this.wsUrl, this.token);

        // loading list of current participants including user
        const participants: ParticipantInfo[] = [];
        this.room.remoteParticipants.forEach(participant => {
          participants.push(this.participantInfoFactory(participant));
        });
        this.participantsSubject$.next(participants);

        // add new participant to ui
        this.room.on(RoomEvent.ParticipantConnected, (participant) => {
          this.addParticipant(participant);
        });

        // remove participant from ui on disconnect
        this.room.on(RoomEvent.ParticipantDisconnected, (participant) => {
          this.removeParticipant(participant);
        });

        this.room.remoteParticipants.forEach(participant => {
          this.registerTrackMutedEvent(participant);
        });

        this.room.remoteParticipants.forEach(participant => {
          this.registerTrackUnmutedEvent(participant);
        });

        // that edge case when track is published first time and change
        // is ignored by trackMuted and trackUnmuted events
        // so ui state brake
        this.room.on(RoomEvent.TrackSubscribed, (track, publication, participant) => {
          track.attach(this.audio);
          const video = document.getElementById('video-' + participant.identity) as HTMLMediaElement;
          track.attach(video);

          const participants = this.participantsSubject$.value;
          const participantIndex = participants.findIndex(p => p.identity === participant.identity);

          if (participantIndex !== -1) {
            const updatedParticipants = [...participants];
            updatedParticipants[participantIndex] = {
              ...updatedParticipants[participantIndex],
              audioEnabled: track.kind === 'audio' ? true : updatedParticipants[participantIndex].audioEnabled,
              videoEnabled: track.kind === 'video' ? true : updatedParticipants[participantIndex].videoEnabled
            };

            this.participantsSubject$.next(updatedParticipants);
          }
        });

        // share data about currently speaking participants
        this.room.on(RoomEvent.ActiveSpeakersChanged, (speakers: Participant[]) => {
          this.speakersSubject$.next(speakers.map(speaker => speaker.identity));
        });

      });
  }

  private addParticipant(participant: Participant){
    const participants = this.participantsSubject$.value;
    participants.push(this.participantInfoFactory(participant));
    this.registerTrackMutedEvent(participant);
    this.registerTrackUnmutedEvent(participant);
    this.participantsSubject$.next(participants);
  }

  private removeParticipant(participant: Participant){
    const participants = this.participantsSubject$.value;
    this.participantsSubject$.next(
      participants.filter(p => p.identity !== participant.identity)
    );
  }

  private registerTrackMutedEvent(participant: Participant){
    participant.on(ParticipantEvent.TrackMuted, (track => {
      const participants = this.participantsSubject$.value;
      participants.map(p => {
        if(p.identity === participant.identity){
          if(track.kind === 'audio'){
            p.audioEnabled = false;
          } else if(track.kind === 'video'){
            p.videoEnabled = false;
          }
        }
        return p;
      });
      this.participantsSubject$.next(participants);
    }));
  }

  private registerTrackUnmutedEvent(participant: Participant){
    participant.on(ParticipantEvent.TrackUnmuted, (track => {
      const participants = this.participantsSubject$.value;
      participants.map(p => {
        if(p.identity === participant.identity){
          if(track.kind === 'audio'){
            p.audioEnabled = true;
          } else if(track.kind === 'video'){
            p.videoEnabled = true;
          }
        }
        return p;
      });
      this.participantsSubject$.next(participants);
    }));
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
      this.room.disconnect().then(this.room = null);
      this.speakersSubject$.next([]);
      this.participantsSubject$.next([]);
    }
  }

  private participantInfoFactory(participant: Participant): ParticipantInfo{
    return {
      identity: participant.identity,
      audioEnabled: participant.isMicrophoneEnabled,
      videoEnabled: participant.isCameraEnabled,
      screenShareEnabled: participant.isScreenShareEnabled
    }
  }
}
