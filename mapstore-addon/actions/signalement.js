export const actions = {
	ATTACHMENT_CONFIGURATION_LOAD: 'SIGNALEMENT:ATTACHMENT_CONFIGURATION:LOAD',
	THEMAS_LOAD: 'SIGNALEMENT:THEMAS:LOAD',
	LAYERS_LOAD: 'SIGNALEMENT:LAYERS:LOAD',
	USER_ME_GET: 'SIGNALEMENT:USER:GET',
	SIGNALEMENT_DRAFT_CREATE: 'SIGNALEMENT:DRAFT:CREATE',
	SIGNALEMENT_DRAFT_CANCEL: 'SIGNALEMENT:DRAFT:CANCEL'		
};

export function loadAttachmentConfiguration(attachmentConfiguration) {
    return {
    	type: ATTACHMENT_CONFIGURATION_LOAD,
    	attachmentConfiguration: attachmentConfiguration
    }
};

export function loadThemas(themas) {
	return {
		type: THEMAS_LOAD,
		themas: themas
	}
};

export function cancelDraft(uuid) {
	return {
		type: SIGNALEMENT_DRAFT_CANCEL,
		uuid: uuid
	}
};