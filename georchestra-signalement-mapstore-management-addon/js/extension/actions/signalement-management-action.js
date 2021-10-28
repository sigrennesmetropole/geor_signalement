export const actions = {
	INIT_SIGNALEMENT: 'SIGNALEMENT:MANAGEMENT:INIT',
	INIT_SIGNALEMENT_DONE: 'SIGNALEMENT:MANAGEMENT:INIT_DONE',		
	CONTEXTS_LOAD: 'SIGNALEMENT:MANAGEMENT:CONTEXTS:LOAD',
	CONTEXTS_LOADED: 'SIGNALEMENT:MANAGEMENT:CONTEXTS:LOADED',
	USER_ME_GET: 'SIGNALEMENT:MANAGEMENT:USER:GET',
	USER_ME_GOT: 'SIGNALEMENT:MANAGEMENT:USER:GOT',
	TASK_GET: 'SIGNALEMENT:MANAGEMENT:TASK:GET',
	TASK_GOT: 'SIGNALEMENT:MANAGEMENT:TASK:GOT',
	INIT_ERROR: 'SIGNALEMENT:MANAGEMENT:INIT:ERROR',
	ACTION_ERROR: 'SIGNALEMENT:MANAGEMENT:ACTION:ERROR',
	ACTION_TASK_ERROR: 'SIGNALEMENT:MANAGEMENT:ACTION_TASK:ERROR',
	OPEN_TABULAR_VIEW: 'SIGNALEMENT:MANAGEMENT:TABULAR_VIEW:OPEN',
	CLOSE_TABULAR_VIEW: 'SIGNALEMENT:MANAGEMENT:TABULAR_VIEW:CLOSE',
	CHANGE_TYPE_VIEW: 'SIGNALEMENT:MANAGEMENT:VIEW:CHANGE',
	TYPE_VIEW_CHANGED: 'SIGNALEMENT:MANAGEMENT:VIEW:CHANGED',
	DISPLAY_MAP_VIEW: 'SIGNALEMENT:MANAGEMENT:VIEW:MAP',
	DOWNLOAD_ATTACHMENT: 'SIGNALEMENT:MANAGEMENT:DOWNLOAD:ATTACHMENT',
	CLAIM_TASK: 'SIGNALEMENT:MANAGEMENT:CLAIM:TASK',
    UPDATE_TASK: 'SIGNALEMENT:MANAGEMENT:UPDATE:TASK',
    UPDATE_DO_ACTION: 'SIGNALEMENT:MANAGEMENT:UPDATE:DO:ACTION'
};

export const status = {
	NOOP: "NOOP",
	INITIALIZED: "Initialized",
};

export const viewType = {
	MY: "MY",
	ADMIN: "ADMIN"
};

export function initSignalementManagement(url){
	return {
		type: actions.INIT_SIGNALEMENT,
		url: url
	}
}

export function initSignalementManagementDone(){
	return {
		type: actions.INIT_SIGNALEMENT_DONE
	}
}

export function loadContexts() {
	return {
		type: actions.CONTEXTS_LOAD
	}
}

export function loadedContexts(contexts) {
	return {
		type: actions.CONTEXTS_LOADED,
		contexts: contexts
	}
}

export function getMe() {
	return {
		type: actions.USER_ME_GET
	}
}

export function gotMe(me) {
	return {
		type: actions.USER_ME_GOT,
		user: me
	}
}

export function getTask(id) {
    return {
        type: actions.TASK_GET,
        id: id
    }
}

export function gotTask(task) {
    return {
        type: actions.TASK_GOT,
        task: task
    }
}

export function downloadAttachment(attachment) {
    return {
        type: actions.DOWNLOAD_ATTACHMENT,
        attachment: attachment
    }
}

export function claimTask(id) {
    return {
        type: actions.CLAIM_TASK,
        id: id
    }
}

export function updateTask(task) {
    return {
        type: actions.UPDATE_TASK,
        task: task
    }
}

export function updateDoAction(actionName, task, viewType) {
    return {
        type: actions.UPDATE_DO_ACTION,
        actionName: actionName,
        task: task,
        viewType: viewType
    }
}

export function openTabularView(context) {
	return {
		type: actions.OPEN_TABULAR_VIEW,
		context : context
	}
}

export function closeTabularView() {
	return {
		type: actions.CLOSE_TABULAR_VIEW,
	}
}

export function changeTypeView(viewType, context) {
	return {
		type: actions.CHANGE_TYPE_VIEW,
		viewType: viewType,
		context: context
	}
}

export function typeViewChanged(viewType, data) {
	return {
		type: actions.TYPE_VIEW_CHANGED,
		viewType: viewType,
		data: data
	}
}

export function displayMapView(data) {
	return {
		type: actions.DISPLAY_MAP_VIEW,
		featureCollection: data
	}
}

function loadError(type, message, e){
	window.signalementMgmt.debug("message:" + message);
	window.signalementMgmt.debug(e);
	return {
		type: type,
		error: {
			message: message,
			e: e
		}
	}
}

export function loadActionError(message, e) {
	return loadError(actions.ACTION_ERROR, message, e);
}

export function loadInitError(message, e) {
	return loadError(actions.INIT_ERROR, message, e);
}

export function loadTaskActionError(message, e) {
    return loadError(actions.ACTION_TASK_ERROR, message, e);
}
