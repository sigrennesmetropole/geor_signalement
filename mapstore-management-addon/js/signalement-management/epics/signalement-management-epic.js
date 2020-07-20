import * as Rx from 'rxjs';
import axios from 'axios';
import {head} from 'lodash';
import {addLayer, changeLayerProperties, updateNode} from '../../../MapStore2/web/client/actions/layers';
import {
    actions,
    displayAdminView,
    displayMapView,
    gotMe,
    loadActionError,
    loadedContexts,
    loadInitError,
    typeViewChanged,
    viewType
} from '../actions/signalement-management-action';
import {closeFeatureGrid, openFeatureGrid, setLayer} from '../../../MapStore2/web/client/actions/featuregrid';

const SIGNALEMENT_MANAGEMENT_LAYER_ID = 'signalements';
const SIGNALEMENT_MANAGEMENT_LAYER_NAME = 'Signalements';

let backendURLPrefix = "http://localhost:8082";

/*export const configureBackendUrl = (value) => {
    console.log("sigm configure backend url:" + value);
    backendURLPrefix = value;
};*/

export const loadContextsEpic = (action$) =>
    action$.ofType(actions.CONTEXTS_LOAD)
        .switchMap((action) => {
            console.log("sigm epics load contexts");
            if (action.contexts) {
                return Rx.Observable.of(loadedContexts(action.contexts)).delay(0);
            }
            console.log("sigm back " + backendURLPrefix);
            const url = backendURLPrefix + "/user/contexts";
            return Rx.Observable.defer(() => axios.get(url))
                .switchMap((response) => Rx.Observable.of(loadedContexts(response.data)))
                .catch(e => Rx.Observable.of(loadInitError("signalement-management.init.contexts.error", e)));
        });

export const loadManagementMeEpic = (action$) =>
    action$.ofType(actions.USER_ME_GET)
        .switchMap((action) => {
            console.log("sigm epics me");
            if (action.user) {
                return Rx.Observable.of(gotMe(action.user)).delay(0);
            }
            const url = backendURLPrefix + "/user/me";
            return Rx.Observable.defer(() => axios.get(url))
                .switchMap((response) => Rx.Observable.of(gotMe(response.data)))
                .catch(e => Rx.Observable.of(loadInitError("signalement-management.init.me.error", e)));
        });

export const loadViewDataEpic = (action$) =>
    action$.ofType(actions.CHANGE_TYPE_VIEW)
        .switchMap((action) => {
            console.log("sigm epics change type view");
            const url = backendURLPrefix + "/task/search/geojson?contextName=" + action.context.name +
                "&asAdmin=" + (action.viewType === viewType.MY ? "false":"true");

            return Rx.Observable.defer(() => axios.get(url))
                .switchMap((response) => Rx.Observable.of(typeViewChanged(action.viewType, response.data)))
                .catch(e => Rx.Observable.of(loadActionError("signalement-management.load.searchTask.error", e)));
        });

export const updateViewDataEpic = (action$, store) =>
    action$.ofType(actions.TYPE_VIEW_CHANGED)
        .switchMap((action) => {
            console.log("sigm epics type view changed");
            // Affichage de la carte
            if (action.viewType === viewType.MY) {
                return Rx.Observable.of(displayMapView(action.data));
            }
            // Affichage de la vue admin
            else {
                return Rx.Observable.of(displayAdminView());
            }
        });

export const displayMapViewDataEpic = (action$, store) =>
    action$.ofType(actions.DISPLAY_MAP_VIEW)
        .switchMap((action) => {
            const signalementsLayer = head(store.getState().layers.flat.filter(l => l.id === SIGNALEMENT_MANAGEMENT_LAYER_ID));
            return Rx.Observable.from((signalementsLayer
                    ? [updateNode(SIGNALEMENT_MANAGEMENT_LAYER_ID, 'layer', {features: createNewFeatures(action)})]
                    : [addLayer({
                        handleClickOnLayer: true,
                        hideLoading: true,
                        id: SIGNALEMENT_MANAGEMENT_LAYER_ID,
                        name: SIGNALEMENT_MANAGEMENT_LAYER_NAME,
                        style: undefined,
                        type: "vector",
                        visibility: true,
                        features: createNewFeatures(action),
                    })]
            ).concat(changeLayerProperties(SIGNALEMENT_MANAGEMENT_LAYER_ID, {visibility: true})));
        });

export const displayAdminViewDataEpic = (action$, store) =>
    action$.ofType(actions.DISPLAY_ADMIN_VIEW)
        .switchMap((action) => {
            const signalementsLayer = head(store.getState().layers.flat.filter(l => l.id === SIGNALEMENT_MANAGEMENT_LAYER_ID));
            return Rx.Observable.from((signalementsLayer
                    ? [changeLayerProperties(SIGNALEMENT_MANAGEMENT_LAYER_ID, {visibility: false})]
                    : []
            ).concat([/* display admin view*/]));
        });

export const openTabularViewEpic = (action$, store) =>
    action$.ofType(actions.OPEN_TABULAR_VIEW)
        .switchMap((action) => {
            console.log("sigm epics open");
            const signalementsLayer = head(store.getState().layers.flat.filter(l => l.id === SIGNALEMENT_MANAGEMENT_LAYER_ID));
            if( signalementsLayer) {
                return Rx.Observable.from(([setLayer(SIGNALEMENT_MANAGEMENT_LAYER_ID)] ).concat([openFeatureGrid()
                ]));
            } else {
                return Rx.Observable.from(
                    [addLayer({
                        type: 'vector',
                        visibility: true,
                        id:SIGNALEMENT_MANAGEMENT_LAYER_ID,
                        name: SIGNALEMENT_MANAGEMENT_LAYER_NAME,
                        rowViewer: viewer,
                        hideLoading: true,
                        style: action.style,
                        handleClickOnLayer: true
                    })]
                ).concat(
                    [
                        setLayer(SIGNALEMENT_MANAGEMENT_LAYER_ID),
                        openFeatureGrid()
                    ]);
            }
        });

export const closeTabularViewEpic = (action$, store) =>
    action$.ofType(actions.CLOSE_TABULAR_VIEW)
        .switchMap((action) => {
            console.log("sigm epics close");
            const signalementsLayer = head(store.getState().layers.flat.filter(l => l.id === SIGNALEMENT_MANAGEMENT_LAYER_ID));
            return Rx.Observable.from((signalementsLayer
                    ? [closeFeatureGrid()]
                    : null
            ));
        });

const createNewFeatures = (action) => {
    return action.featureCollection && action.featureCollection.features
        ? action.featureCollection.features
        : [];
};
