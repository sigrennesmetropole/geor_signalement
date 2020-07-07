export const actions = {
	ATTACHMENT_CONFIGURATION_LOAD: 'SIGNALEMENT:ATTACHMENT_CONFIGURATION:LOAD',
	ATTACHMENT_CONFIGURATION_LOADED: 'SIGNALEMENT:ATTACHMENT_CONFIGURATION:LOADED',
	THEMAS_LOAD: 'SIGNALEMENT:THEMAS:LOAD',
	THEMAS_LOADED: 'SIGNALEMENT:THEMAS:LOADED',
	LAYERS_LOAD: 'SIGNALEMENT:LAYERS:LOAD',
	LAYERS_LOADED: 'SIGNALEMENT:LAYERS:LOADED',
	USER_ME_GET: 'SIGNALEMENT:USER:GET',
	USER_ME_GOT: 'SIGNALEMENT:USER:GOT',
	SIGNALEMENT_OPEN_PANEL: 'SIGNALEMENT:PANEL:OPEN',
	SIGNALEMENT_CLOSE_PANEL: 'SIGNALEMENT:PANEL:CLOSE',
	SIGNALEMENT_DRAFT_CREATE: 'SIGNALEMENT:DRAFT:CREATE',
	SIGNALEMENT_DRAFT_CREATED: 'SIGNALEMENT:DRAFT:CREATED',
	SIGNALEMENT_TASK_CREATE: 'SIGNALEMENT:TASK:CREATE',
	SIGNALEMENT_TASK_CREATED: 'SIGNALEMENT:TASK:CREATED',
	ADD_ATTACHMENT: 'SIGNALEMENT:ATTACHMENT:ADD',
	ATTACHMENT_ADDED: 'SIGNALEMENT:ATTACHMENT:ADDED',
	REMOVE_ATTACHMENT: 'SIGNALEMENT:ATTACHMENT:REMOVE',
	ATTACHMENT_REMOVED: 'SIGNALEMENT:ATTACHMENT:REMOVED',
	SIGNALEMENT_CLOSING: 'SIGNALEMENT:CLOSING',
	SIGNALEMENT_CANCEL_CLOSING: 'SIGNALEMENT:CANCEL:CLOSING',
	SIGNALEMENT_CONFIRM_CLOSING: 'SIGNALEMENT:CONFIRM:CLOSING',
	SIGNALEMENT_DRAFT_CANCEL: 'SIGNALEMENT:DRAFT:CANCEL',
	SIGNALEMENT_DRAFT_CANCELED: 'SIGNALEMENT:DRAFT:CANCELED',
	INIT_ERROR: 'SIGNALEMENT:INIT:ERROR',
	ACTION_ERROR: 'SIGNALEMENT:ACTION:ERROR'
};

export const status = {
	NO_TASK: "NOOP",
	LOAD_TASK: "loadTask",
	TASK_INITIALIZED: "taskInitialized",	
	CREATE_TASK: "createTask",
	TASK_CREATED: "taskCreated",
	REQUEST_UNLOAD_TASK: "requestUnloadTask",
	UNLOAD_TASK: "unloadTask",
	TASK_UNLOADED: "taskUnloaded"
};

export function loadAttachmentConfiguration() {
    return {
    	type: actions.ATTACHMENT_CONFIGURATION_LOAD
    }
};

export function loadedAttachmentConfiguration(attachmentConfiguration) {
    return {
    	type: actions.ATTACHMENT_CONFIGURATION_LOADED,
    	attachmentConfiguration: attachmentConfiguration
    }
};

export function addAttachment(attachment) {
    console.log('add file action: ' + attachment)
    return {
        type: actions.ADD_ATTACHMENT,
        attachment : attachment
    }
};

export function addedAttachment(attachment) {
    console.log('added file action: ' + attachment)
    return {
        type: actions.ATTACHMENT_ADDED,
        attachment : attachment
    }
};

export function removeAttachment(attachment) {
    console.log('add file action: ' + attachment)
    return {
        type: actions.REMOVE_ATTACHMENT,
        attachment : attachment
    }
};

export function removedAttachment(attachmentIndex) {
    console.log('remove file action: ' + attachmentIndex)
    return {
        type: actions.ATTACHMENT_REMOVED,
        attachmentIndex : attachmentIndex
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

export function createDraft(context) {
	return {
		type: actions.SIGNALEMENT_DRAFT_CREATE,
		status: status.LOAD_TASK,
		context: context
	}
};

export function draftCreated(task) {
	return {
		type: actions.SIGNALEMENT_DRAFT_CREATED,
		status: status.TASK_INITIALIZED,
		task: task,
		error: null,
	}
};

export function cancelDraft(uuid) {
	return {
		type: actions.SIGNALEMENT_DRAFT_CANCEL,
		status: status.UNLOAD_TASK,
		uuid: uuid
	}
};

export function createTask(task) {
	return {
		type: actions.SIGNALEMENT_TASK_CREATE,
		status: status.CREATE_TASK,
		task: task
	}
};

export function taskCreated(task) {
	return {
		type: actions.SIGNALEMENT_TASK_CREATED,
		status: status.TASK_CREATED,
		task: task,
		error: null,
	}
};

export function draftCanceled() {
	return {
		type: actions.SIGNALEMENT_DRAFT_CANCELED,
		status: status.TASK_UNLOADED,
		task: null,
		error: null,
	}
};

export function requestClosing() {
	return {
		type: actions.SIGNALEMENT_CLOSING,
		status: status.REQUEST_UNLOAD_TASK
	}
};

export function cancelClosing() {
	return {
		type: actions.SIGNALEMENT_CANCEL_CLOSING,
	}
};

export function confirmClosing() {
	return {
		type: actions.SIGNALEMENT_CONFIRM_CLOSING,
	}
};

function loadError(type, message, e){
	console.log("message:" + message);
	console.log(e);
	return {
		type: type,
		error: {
			message: message,
			e: e
		}
	}
};

export function loadActionError(message, e) {
	return loadError(actions.ACTION_ERROR, message, e);
};

export function loadInitError(message, e) {
	return loadError(actions.INIT_ERROR, message, e);
};

export function openPanel(currentLayer) {
	return {
		type: actions.SIGNALEMENT_OPEN_PANEL,
		currentLayer: currentLayer
	}
};

export function closePanel(){
	return {
		type: actions.SIGNALEMENT_CLOSE_PANEL
	}
};
