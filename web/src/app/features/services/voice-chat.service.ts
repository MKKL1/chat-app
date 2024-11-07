import {Injectable, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {VoiceChannelQuery} from "../store/voiceChannel/voice.channel.query";
import {environment} from "../../../environment";
import {
  LocalAudioTrack,
  LocalVideoTrack,
  Participant,
  ParticipantEvent, RemoteParticipant,
  RemoteTrack, RemoteTrackPublication,
  Room,
  RoomEvent
} from "livekit-client";
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class VoiceChatService{

  token: string;
  wsUrl: string = 'ws://localhost:7880';

  room: Room | null = null;

  audio: HTMLMediaElement;

  participantsSubject$ = new BehaviorSubject<string[]>([]);
  speakersSubject$ = new BehaviorSubject<string[]>([]);

  constructor(
    private http: HttpClient,
    private voiceChannelQuery: VoiceChannelQuery) {

    this.audio = document.getElementById('audio-player') as HTMLMediaElement;
  }

  async joinRoom(): Promise<void>{
    const channelId = this.voiceChannelQuery.getActiveId();
    this.http.get(environment.api + `channels/${channelId}/voice/join`)
      .subscribe(async (res: any) => {
        this.token = res.token;

        if (this.room !== null) {
          this.room.disconnect()
            .then(this.room = null);
        }

        this.room = new Room();
        await this.room.connect(this.wsUrl, this.token);

        // loading list of current participants including user
        const participantsIds: string[] = [];
        participantsIds.push(this.room.localParticipant.identity);
        this.room.remoteParticipants.forEach(participant => {
            participantsIds.push(participant.identity);
        });
        this.participantsSubject$.next(participantsIds);

        this.room.on(RoomEvent.ParticipantConnected, (participant) => {
          console.log(`User connected: ${participant.identity}`);
          this.addParticipant(participant);
        });

        this.room.on(RoomEvent.ParticipantDisconnected, (participant) => {
          console.log(`User disconnected: ${participant.identity}`);
          this.removeParticipant(participant);
        });

        this.room.on(RoomEvent.ActiveSpeakersChanged, (speakers: Participant[]) => {
          //speakers.forEach(s => console.log(s));
          this.speakersSubject$.next(speakers.map(speaker => speaker.identity));
        });

        this.room.remoteParticipants.forEach(participant => {
          participant.on(ParticipantEvent.IsSpeakingChanged, (speaking: boolean) => {
            // console.log(
            //   `${participant.identity} is ${speaking ? 'now' : 'no longer'} speaking. audio level: ${participant.audioLevel}`,
            // );
          });
        })

        this.room.on(RoomEvent.TrackSubscribed, this.handleTrackSubscribed)

        this.setCamera(true);
        this.setMicrophone(true);
      });
  }

  private handleTrackSubscribed(track: RemoteTrack, publication: RemoteTrackPublication, participant: RemoteParticipant){
    track.attach(this.audio);
  }

  private addParticipant(participant: Participant){
    const participants = this.participantsSubject$.value;
    participants.push(participant.identity);
    this.participantsSubject$.next(participants);
  }

  private removeParticipant(participant: Participant){
    const participants = this.participantsSubject$.value;
    this.participantsSubject$.next(
      participants.filter(p => p !== participant.identity)
    );
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
      this.speakersSubject$.next([]);
      this.participantsSubject$.next([]);
    }
  }
}
