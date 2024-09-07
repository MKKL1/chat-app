import { TestBed } from '@angular/core/testing';

import { RsocketService } from './rsocket.service';

describe('RsocketService', () => {
  let service: RsocketService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RsocketService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
