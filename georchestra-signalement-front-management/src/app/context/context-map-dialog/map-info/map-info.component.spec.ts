import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MapInfo } from './map-info.component';

describe('MapInfoComponent', () => {
  let component: MapInfo;
  let fixture: ComponentFixture<MapInfo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MapInfo ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MapInfo);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
