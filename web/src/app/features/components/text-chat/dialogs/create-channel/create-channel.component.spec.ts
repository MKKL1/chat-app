import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CreateChannelComponent } from './create-channel.component';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {ChannelType} from "../../../../models/channel";
import {MatSnackBar} from "@angular/material/snack-bar";
import {ChannelService} from "../../../../services/channel.service";
import {NoopAnimationsModule} from "@angular/platform-browser/animations";

const matDataObjMock = {
  editing: false,
  channel: {
    id: '',
    name: '',
    communityId: '',
    type: ChannelType.Text
  }
};

const channelServiceMock = {
  createChannel: jest.fn(),
  editChannel: jest.fn(),
  deleteChannel: jest.fn()
};

describe('CreateChannelComponent', () => {
  let component: CreateChannelComponent;
  let fixture: ComponentFixture<CreateChannelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateChannelComponent, NoopAnimationsModule],
      providers: [
        MatSnackBar,
        {provide: MatDialogRef, useValue: {}},
        {provide: MAT_DIALOG_DATA, useValue: matDataObjMock},
        {provide: ChannelService, useValue: channelServiceMock}
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateChannelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
