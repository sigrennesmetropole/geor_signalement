export const actions = {
	ATTACHMENT_CONFIGURATION_LOAD: 'SIGNALEMENT:ATTACHMENT_CONFIGURATION:LOAD',
	ATTACHMENT_CONFIGURATION_LOADED: 'SIGNALEMENT:ATTACHMENT_CONFIGURATION:LOADED',
	THEMAS_LOAD: 'SIGNALEMENT:THEMAS:LOAD',
	THEMAS_LOADED: 'SIGNALEMENT:THEMAS:LOADED',
	LAYERS_LOAD: 'SIGNALEMENT:LAYERS:LOAD',
	LAYERS_LOADED: 'SIGNALEMENT:LAYERS:LOADED',
	USER_ME_GET: 'SIGNALEMENT:USER:GET',
	USER_ME_GOT: 'SIGNALEMENT:USER:GOT',
	SIGNALEMENT_DRAFT_CREATE: 'SIGNALEMENT:DRAFT:CREATE',
	SIGNALEMENT_DRAFT_CANCEL: 'SIGNALEMENT:DRAFT:CANCEL',
	INIT_ERORR: 'SIGNALEMENT:INIT:ERROR'		
};

export function loadAttachmentConfiguration() {
	console.log('action loadAttachmentConfiguration');
    return {
    	type: actions.ATTACHMENT_CONFIGURATION_LOAD
    }
};

export function loadedAttachmentConfiguration(attachmentConfiguration) {
	console.log('action loadedAttachmentConfiguration');
    return {
    	type: actions.ATTACHMENT_CONFIGURATION_LOADED,
    	attachmentConfiguration: attachmentConfiguration
    }
};

export function loadThemas() {
	return {
		type: actions.THEMAS_LOAD
	}
};

export function loadedThemas(themas) {
	return {
		type: actions.THEMAS_LOADED,
		themas: themas
	}
};

export function loadLayers() {
	return {
		type: actions.LAYERS_LOAD
	}
};

export function loadedLayers(layers) {
	return {
		type: actions.LAYERS_LOADED,
		layers: layers
	}
};

export function getMe() {
	return {
		type: actions.USER_ME_GET
	}
};

export function gotMe(me) {
	return {
		type: actions.USER_ME_GOT,
		user: me
	}
};

export function cancelDraft(uuid) {
	return {
		type: actions.SIGNALEMENT_DRAFT_CANCEL,
		uuid: uuid
	}
};

export function createDraft(context) {
	return {
		type: actions.SIGNALEMENT_DRAFT_CREATE,
		context: context
	}
};

export function loadError(message, e) {
	console.log("message:" + message);
	console.log(e);
	return {
		type: actions.INIT_ERORR,
		error: {
			message: message,
			e: e
		}
	}
};