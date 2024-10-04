import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TextChannelComponent } from './text-channel.component';
import {TextChannelQuery} from "../../../store/textChannel/text.channel.query";

describe('TextChannelComponent', () => {
  let component: TextChannelComponent;
  let fixture: ComponentFixture<TextChannelComponent>;
  let channelQuery: TextChannelQuery;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TextChannelComponent],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TextChannelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    channelQuery = TestBed.inject(TextChannelQuery);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
