const Rx = require('rxjs');
const axios = require('../../../web/client/libs/ajax');
const uuidv1 = require('uuid/v1');
const {actions, loadAttachmentConfiguration} = require('../actions/signalement');
const assign = require('object-assign');

module.exports = (config) => ({
    loadAttachmentConfigurationEpic: (action$) => action$.ofType(actions.ATTACHMENT_CONFIGURATION_LOAD)
        .switchMap((action) => {
        	if (action.attachmentConfiguration) {
                return Rx.Observable.of(loadAttachmentConfiguration(action.attachmentConfiguration)).delay(0);
            }
            const url = "/reporting/attachment/configuration"
            return Rx.Observable.defer(() => axios.get(url))
                .switchMap((response) => Rx.Observable.of(loadAttachmentConfiguration(config.readFields(response.data))))
                .catch(e => Rx.Observable.of(loadError("Failed to load attachment configuration", e)));
        })

});
