import {Component, Inject, OnInit} from '@angular/core';
import TileLayer from "ol/layer/Tile";
import {OSM} from "ol/source";
import {fromLonLat} from "ol/proj";
import {Feature, Overlay, View} from "ol";
import Map from 'ol/Map';
import VectorLayer from "ol/layer/Vector";
import {GeoJSON} from "ol/format";
import VectorSource from "ol/source/Vector";
import {Fill, Stroke, Style} from "ol/style";
import {bbox as bboxStrategy} from 'ol/loadingstrategy';
import {Geometry} from "ol/geom";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import Text from 'ol/style/Text';
import {click, pointerMove} from "ol/events/condition";
import {Select} from "ol/interaction";

@Component({
  selector: 'context-map-dialog',
  templateUrl: './context-map-dialog.component.html',
  styleUrls: ['./context-map-dialog.component.scss']
})
export class ContextMapDialog implements OnInit {

  map ?: Map;
  data : any;
  vectorLayer?: VectorLayer<VectorSource<Geometry>>
  popUp?: Overlay;
  currentFeature?: Feature
  clickInteraction?: Select

  defaultFillColor = 'rgba(80,80,80,0.3)';
  defaultStrokeColor = '#28282888';
  defaultWidth = 3

  constructor(@Inject(MAT_DIALOG_DATA) data:any) {
    this.data = data.target;
  }

  ngOnInit(): void {
    this.initMap()
    this.map?.addLayer(this.createWFSLayer(this.data));

    this.popUp = new Overlay({
      element: document.getElementById('mapInfo')!,
      autoPan: true,
      autoPanAnimation: {
        duration: 250,
      },
      offset: [9, 9]
    });

    // Style sélectionné
    const selectStyleFunc = (feature: Feature) => {
      return new Style({
        text: this.getLibelle(feature),
        fill: new Fill({
          color: 'rgba(40,40,40,0.7)',
        }),
        stroke: new Stroke({
          color: '#282828FF',
          width: this.defaultWidth + 1,
        }),
      });
    };

    this.clickInteraction = new Select({
      condition: click,
      // @ts-ignore
      style: selectStyleFunc,
      toggleCondition: null!,
      multi:false,
      layers: [this.vectorLayer!],
    });

    const HoverInteraction = new Select({
      condition: pointerMove,
      // @ts-ignore
      style: selectStyleFunc,
      toggleCondition: null!,
      multi:false,
      layers: [this.vectorLayer!],
    });

    const onClickCallback = (feature: Feature, selected: boolean, mouseCoordinate: any) => {
      if (selected) {
        // Affichage de la popup
        this.currentFeature = feature;
        this.currentFeature.set("olLayer", this.vectorLayer);
        this.openPopup(mouseCoordinate);
      }
    };

    //this.vectorLayer._clickInteraction = clickInteraction;
    this.map?.addInteraction(this.clickInteraction);
    this.map?.addInteraction(HoverInteraction);

    this.clickInteraction.on('select', (e) => {
      // Récupération de la feature sélectionné OU déselectionné
      const featureClicked = e.selected[0] || e.deselected[0];

      let selected: boolean;

      selected = e.selected[0] != null;
      if (featureClicked != null && onClickCallback != null) {
        onClickCallback(featureClicked, selected, e.mapBrowserEvent.coordinate);
      }
    });


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

  private getLibelle(feature: Feature): Text {
    // Création du libellé à afficher
    return new Text({
      text: feature.get('label'),
      stroke: new Stroke({ color: 'white', width: 1 }),
      font: 'bold 12px sans-serif',
    });
  }

  public openPopup(mouseCoordinate: any) {
    this.map?.addOverlay(this.popUp!);
    this.popUp?.setPositioning('bottom-right');

    // Affichage de la popup
    this.popUp?.setPosition(mouseCoordinate);
  }


}
