import {Component, inject, Input, OnDestroy, OnInit, signal} from '@angular/core';
import {MatListModule} from "@angular/material/list";
import {DatePipe} from "@angular/common";
import {ShorteningPipe} from "../../../../shared/pipes/ShorteningPipe";
import {Channel} from "../../../models/channel";
import {MatIconButton, MatMiniFabButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatBadge} from "@angular/material/badge";
import {MessageNotification} from "../../../models/message-notification";
import {Subscription} from "rxjs";
import {EventService} from "../../../../core/events/event.service";

@Component({
  selector: 'app-text-channel-info',
  standalone: true,
  imports: [
    MatListModule,
    DatePipe,
    ShorteningPipe,
    MatMiniFabButton,
    MatIcon,
    MatIconButton,
    MatBadge
  ],
  templateUrl: './text-channel-info.component.html',
  styleUrl: './text-channel-info.component.scss'
})
export class TextChannelInfoComponent implements OnInit, OnDestroy{
  @Input() channel: Channel | undefined;

  private eventService = inject(EventService);
  private notificationSubscription: Subscription;

  notification = signal<MessageNotification | null>(null);
  count = signal<number>(0);

  // TODO add variables for data which will be displayed
  username: string = "User 1";
  // Testing pipe which will transform it to shorter form
  message: string = "Example messageExample messageExample messageExample messageExample messageExample message";
  date: Date = new Date();

  ngOnInit() {
    this.notificationSubscription = this.eventService.notification$.subscribe((notification) => {
      if(notification.channelId === this.channel?.id){
        this.count.update(x => x + 1);
        this.notification.set(notification);
      }
    });
  }

  clearNotifications(){
    this.count.set(0);
    this.notification.set(null);
  }

  ngOnDestroy() {
    if(this.notificationSubscription){
      this.notificationSubscription.unsubscribe();
    }
  }
}
