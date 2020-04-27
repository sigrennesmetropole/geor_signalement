const Rx = require('rxjs');
const axios = require('../libs/ajax');
const uuidv1 = require('uuid/v1');
const {actions, loadAttachmentConfiguration} = require('../actions/signalement');
const assign = require('object-assign');

module.exports = (config) => ({
    newAnnotationEpic: (action$) => action$.ofType(actions.ATTACHMENT_CONFIGURATION_LOAD)
        .switchMap((action) => {
        	if (action.attachmentConfiguration) {
                return Rx.Observable.of(fieldsLoaded(action.layer, action.layer.thematic.fields)).delay(0);
            }
            const url = "/reporting/attachment/configuration"
            return Rx.Observable.defer(() => axios.get(url))
                .switchMap((response) => Rx.Observable.of(fieldsLoaded(action.layer, config.readFields(response.data))))
                .catch(e => Rx.Observable.of(fieldsError(action.layer, e)));
        })

});
