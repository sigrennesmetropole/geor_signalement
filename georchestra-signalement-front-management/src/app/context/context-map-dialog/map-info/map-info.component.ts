import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {Feature, Overlay} from "ol";
import {MatDialogRef} from "@angular/material/dialog";
import {Select} from "ol/interaction";

@Component({
    selector: 'map-info',
    templateUrl: './map-info.component.html',
    styleUrls: ['./map-info.component.scss']
})
export class MapInfo implements OnChanges {
    @Input() feature?: Feature
    @Input() popUp?: Overlay
    @Input() select?: Select
    @Output()

    firstNameOperator?: string
    lastNameOperator?: string
    roleOperator?: string
    nameContext?: string
    nameArea?: string

    close: EventEmitter<any> = new EventEmitter<any>();


    constructor(public dialogRef: MatDialogRef<MapInfo>) {
    }

    onClose() {
        this.popUp?.setMap(null);
        this.select?.getFeatures().clear();
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.firstNameOperator = this.feature?.getProperties()["first_name"]
        this.lastNameOperator = this.feature?.getProperties()["last_name"]
        this.roleOperator = this.feature?.getProperties()["role_name"]
        this.nameContext = this.feature?.getProperties()["context_name"]
        this.nameArea = this.feature?.getProperties()["area_name"]
    }
}
