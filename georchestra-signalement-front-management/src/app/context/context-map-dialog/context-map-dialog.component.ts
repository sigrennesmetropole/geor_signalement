import {Component, OnInit} from '@angular/core';
import TileLayer from "ol/layer/Tile";
import {OSM} from "ol/source";
import {fromLonLat} from "ol/proj";
import {View} from "ol";
import Map from 'ol/Map';

@Component({
  selector: 'context-map-dialog',
  templateUrl: './context-map-dialog.component.html',
  styleUrls: ['./context-map-dialog.component.scss']
})
export class ContextMapDialog implements OnInit {

  map ?: Map;

  constructor() {
  }

  ngOnInit(): void {
    this.initMap()
  }

  initMap() {
    this.map = new Map({
      target: 'mapRef',
      layers: [
        new TileLayer({
          source: new OSM(),
        }),
      ],
      view: new View({
        center: fromLonLat([-1.651, 48.119], 'EPSG:3857'),
        zoom: 11,
        projection: 'EPSG:3857'
      })
    });
  }

}
