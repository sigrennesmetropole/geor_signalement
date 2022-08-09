import {Component, Inject, OnInit} from '@angular/core';
import TileLayer from "ol/layer/Tile";
import {OSM} from "ol/source";
import {fromLonLat} from "ol/proj";
import {View} from "ol";
import Map from 'ol/Map';
import VectorLayer from "ol/layer/Vector";
import {GeoJSON} from "ol/format";
import VectorSource from "ol/source/Vector";
import {Fill, Stroke, Style} from "ol/style";
import {bbox as bboxStrategy} from 'ol/loadingstrategy';
import {Geometry} from "ol/geom";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

@Component({
  selector: 'context-map-dialog',
  templateUrl: './context-map-dialog.component.html',
  styleUrls: ['./context-map-dialog.component.scss']
})
export class ContextMapDialog implements OnInit {

  map ?: Map;
  data : any;
  vectorLayer?: VectorLayer<VectorSource<Geometry>>

  defaultFillColor = 'rgba(25, 165, 240, 0.2)';
  defaultStrokeColor = '#3399CC';
  defaultWidth = 3

  constructor(@Inject(MAT_DIALOG_DATA) data:any) {
    this.data = data.target;
  }

  ngOnInit(): void {
    this.initMap()
    this.map?.addLayer(this.createWFSLayer(this.data));
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

  createWFSLayer(contextItem:any): VectorLayer<any> {
    var vectorSource = new VectorSource({
      format: new GeoJSON(),
      url: function (extent) {
        return (
            '/geoserver/wfs?service=WFS&' +
            'version=1.1.0&request=GetFeature&typename=' + 'signalement:context_geographic_area' + '&' +
            'outputFormat=application/json&srsname=' + 'EPSG:3857' + '&' +
            '&CQL_FILTER=context_name=\''+contextItem.name+'\''
        );
      },
      strategy: bboxStrategy,
    });

    this.vectorLayer = new VectorLayer({
      source: vectorSource,
      style: new Style({
        stroke: new Stroke({
          color: this.defaultStrokeColor,
          width: this.defaultWidth,
        }),
        fill: new Fill({
          color: this.defaultFillColor
        })
      }),
      minZoom: 7,
      properties: {'title': 'Zones'}
    });

    return this.vectorLayer;
  }


}
