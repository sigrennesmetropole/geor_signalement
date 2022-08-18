import {Component, Inject, OnInit} from '@angular/core';
import TileLayer from "ol/layer/Tile";
import {WMTS} from "ol/source";
import {fromLonLat, get as getProjection} from "ol/proj";
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
import WMTSTileGrid from "ol/tilegrid/WMTS";
import {getTopLeft, getWidth} from "ol/extent";
import {FlowMapConfiguration} from "../../api/models/flow-map-configuration";
import {ViewMapConfiguration} from "../../api/models/view-map-configuration";
import {StyleMapConfiguration} from "../../api/models/style-map-configuration";

@Component({
    selector: 'context-map-dialog',
    templateUrl: './context-map-dialog.component.html',
    styleUrls: ['./context-map-dialog.component.scss']
})
export class ContextMapDialog implements OnInit {

    map ?: Map;
    data: any;
    vectorLayer?: VectorLayer<VectorSource<Geometry>>
    popUp?: Overlay;
    currentFeature?: Feature
    clickInteraction?: Select
    flowMap ?: FlowMapConfiguration
    viewMap ?: ViewMapConfiguration
    styleMap ?: StyleMapConfiguration

    defaultFillColor = 'rgba(80,80,80,0.3)';
    defaultStrokeColor = '#28282888';
    defaultWidth = 3

    constructor(@Inject(MAT_DIALOG_DATA) data: any) {

        this.viewMap = data.viewMap;
        this.flowMap = data.flowMap;
        this.styleMap = data.colorEasement;
        this.data = data.target;
        this.defaultFillColor = this.styleMap?.["fill-color"]!;
        this.defaultStrokeColor = this.styleMap?.["stoke-color"]!;
    }

    ngOnInit(): void {
        //Initialisation à partir du fond de carte configuré dans le .properties
        this.initMap(this.viewMap!, this.flowMap!)

        //Ajout des emprises du context
        this.map?.addLayer(this.createWFSLayer(this.data));

        // Style d'une emprise sélectionné
        const selectStyleFunc = (feature: Feature) => {
            return new Style({
                text: this.getLibelle(feature),
                fill: new Fill({
                    color: this.styleMap?.["fill-color-hover"],
                }),
                stroke: new Stroke({
                    color: this.styleMap?.["stroke-color-hover"],
                    width: this.defaultWidth + 1,
                }),
            });
        };

        //Initialisation PopUp
        this.popUp = new Overlay({
            element: document.getElementById('mapInfo')!,
            autoPan: true,
            autoPanAnimation: {
                duration: 250,
            },
            offset: [9, 9]
        });

        //Évènement click sur une emprise
        this.clickInteraction = new Select({
            condition: click,
            // @ts-ignore
            style: selectStyleFunc,
            toggleCondition: null!,
            multi: false,
            layers: [this.vectorLayer!],
        });

        const HoverInteraction = new Select({
            condition: pointerMove,
            // @ts-ignore
            style: selectStyleFunc,
            toggleCondition: null!,
            multi: false,
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

    initMap(viewMap: ViewMapConfiguration, flowMap: FlowMapConfiguration) {

        this.map = new Map({
            target: 'mapRef',
            layers: [],
            view: new View({
                center: fromLonLat([parseFloat(viewMap.x!), parseFloat(viewMap.y!)], flowMap.projection),
                zoom: 11,
                projection: flowMap.projection,
            })
        });

        this.addLayerFromAWmtsFlow(flowMap);
    }

    createWFSLayer(contextItem: any): VectorLayer<any> {
        var vectorSource = new VectorSource({
            format: new GeoJSON(),
            url: function (extent) {
                return (
                    '/geoserver/wfs?service=WFS&' +
                    'version=1.1.0&request=GetFeature&typename=' + 'signalement:context_geographic_area' + '&' +
                    'outputFormat=application/json&srsname=' + 'EPSG:3857' + '&' +
                    '&CQL_FILTER=context_name=\'' + contextItem.name + '\''
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

    public openPopup(mouseCoordinate: any) {
        this.map?.addOverlay(this.popUp!);
        this.popUp?.setPositioning('bottom-right');

        // Affichage de la popup
        this.popUp?.setPosition(mouseCoordinate);
    }

    private addLayerFromAWmtsFlow(flowMap: FlowMapConfiguration) {
        const projection = getProjection('EPSG:3857');
        const projectionExtent = projection!.getExtent();
        const size = getWidth(projectionExtent) / 256;
        let resolutions = new Array(19);
        let matrixIds = new Array(19);
        for (let z = 0; z < 19; ++z) {
            // generate resolutions and matrixIds arrays for this WMTS
            resolutions[z] = size / Math.pow(2, z);
            matrixIds[z] = flowMap.matrixId! + z
        }

        this.map?.addLayer(new TileLayer({
                source: new WMTS({
                    url: flowMap.url,
                    layer: flowMap.layer!,
                    matrixSet: flowMap.matrixSet!,
                    version: flowMap.version,
                    format: flowMap.format,
                    projection: flowMap.projection,
                    tileGrid: new WMTSTileGrid({
                        origin: getTopLeft(projectionExtent),
                        resolutions: resolutions,
                        matrixIds: matrixIds,
                    }),
                    style: flowMap.style!,
                })
            })
        );
    }

    private getLibelle(feature: Feature): Text {
        // Création du libellé à afficher
        return new Text({
            text: feature.get('label'),
            stroke: new Stroke({color: 'white', width: 1}),
            font: 'bold 12px sans-serif',
        });
    }
}
