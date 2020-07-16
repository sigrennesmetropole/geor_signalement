export const actions = {
	CONTEXTS_LOAD: 'SIGNALEMENT:MANAGEMENT:CONTEXTS:LOAD',
	CONTEXTS_LOADED: 'SIGNALEMENT:MANAGEMENT:CONTEXTS:LOADED',
	USER_ME_GET: 'SIGNALEMENT:MANAGEMENT:USER:GET',
	USER_ME_GOT: 'SIGNALEMENT:MANAGEMENT:USER:GOT',
	INIT_ERROR: 'SIGNALEMENT:MANAGEMENT:INIT:ERROR',
	ACTION_ERROR: 'SIGNALEMENT:MANAGEMENT:ACTION:ERROR',
	OPEN_TABULAR_VIEW: 'SIGNALEMENT:MANAGEMENT:TABULAR_VIEW:OPEN',
	CLOSE_TABULAR_VIEW: 'SIGNALEMENT:MANAGEMENT:TABULAR_VIEW:CLOSE',
	CHANGE_TYPE_VIEW: 'SIGNALEMENT:MANAGEMENT:VIEW:CHANGE',
	TYPE_VIEW_CHANGED: 'SIGNALEMENT:MANAGEMENT:VIEW:CHANGED',
	DISPLAY_MAP_VIEW: 'SIGNALEMENT:MANAGEMENT:VIEW:MAP',
	DISPLAY_ADMIN_VIEW: 'SIGNALEMENT:MANAGEMENT:VIEW:ADMIN',
};

export const status = {
	NOOP: "NOOP",
	INITIALIZED: "Initialized",
};

export const viewType = {
	MY: "MY",
	ADMIN: "ADMIN"
};

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

export function openTabularView() {
	return {
		type: actions.OPEN_TABULAR_VIEW,
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

export function displayAdminView() {
	return {
		type: actions.DISPLAY_ADMIN_VIEW
	}
}

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
}

export function loadActionError(message, e) {
	return loadError(actions.ACTION_ERROR, message, e);
}

export function loadInitError(message, e) {
	return loadError(actions.INIT_ERROR, message, e);
}
