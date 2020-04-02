/*global
 Ext, GeoExt, OpenLayers, GEOR
 */
Ext.namespace("GEOR.Addons", "GEOR.data");

/**
 *  GEOR.data.NoteStore - JsonStore representing a " note de renseignement signalement
 *
 */
(function () {
    var NoteStore = Ext.extend(Ext.data.JsonStore, {
        constructor: function (config) {
            config = Ext.apply({
                root: "",
                fields: ["user",
                    "task",
                    "themas",
                    "layers",
                    "attachmentConfiguration"
                ]
                //Add proxy configuration here if we want to upload Note data to server
            }, config);

            NoteStore.superclass.constructor.call(this, config);
        },
        updateUser: function (user) {
            var noteRecord = this.getAt(0).copy();
            noteRecord.set("user", user);
            this.add([noteRecord]);
        },
        updateTask: function (task) {
            var noteRecord = this.getAt(0).copy();
            noteRecord.set("task", task);
            this.add([noteRecord]);
        },
        updateThemas: function (themas) {
            var noteRecord = this.getAt(0).copy();
            noteRecord.set("themas", themas);
            this.add([noteRecord]);
        },
        updateLayers: function (layers) {
            var noteRecord = this.getAt(0).copy();
            noteRecord.set("layers", layers);
            this.add([noteRecord]);
        },
        updateAttachmentConfiguration: function (attachmentConfiguration) {
            var noteRecord = this.getAt(0).copy();
            noteRecord.set("attachmentConfiguration", attachmentConfiguration);
            this.add([noteRecord]);
        },
        updateLocalisation: function (points) {
            this.getTask().asset.localization = points;
        },
        getUser: function () {
            return this.getAt(0).get("user");
        },
        getThemas: function () {
            return this.getAt(0).get("themas");
        },
        getLayers: function () {
            return this.getAt(0).get("layers");
        },
        getTask: function () {
            return this.getAt(0).get("task");
        },
        getAttachmentConfiguration: function () {
            return this.getAt(0).get("attachmentConfiguration");
        }
    });

    GEOR.data.NoteStore = NoteStore;

}());


/**
 * Urbanisme addon
 */
GEOR.Addons.Signalement = Ext.extend(GEOR.Addons.Base, {
    /**
     * Window containing the « note de signalement » - {Ext.Window}
     */
    signalementWindow: null,

    /**
     * Informations retrieved from addon server about « user »
     */
    userStore : null,

    /**
     * Data representing a Note de renseignement de signalement - {Object}
     */
    noteStore: new GEOR.data.NoteStore(),

    /**
     * api: config[encoding] ``String`` The encoding to set in the headers when
     * requesting the print service. Prevent character encoding issues,
     * especially when using IE. Default is retrieved from document charset or
     * characterSet if existing or ``UTF-8`` if not.
     */
    encoding: document.charset || document.characterSet || "UTF-8",
    
    /**
     * fields used to build tree Action Menu item
     */
    title : null,
    iconCls: null,
    qtip: null,
    
    /**
     * field used to store current layer record
     */
    layerRecord: null,

    init: function (record) {
    	
    	initActionMenuDate();

        this.userStore = new Ext.data.JsonStore({
            root: "",
            fields: [
                "login",
                "firstName",
                "lastName",
                "email",
                "organisation"
            ],
            proxy: new Ext.data.HttpProxy({
                method: "GET",
                url: this.options.signalementURL + "user/me"
            }),
            listeners: {
                "load": {
                    fn: function(store, records) {
                        //We assume there is only 1 returned record
                        this.log("load :", records[0]);
                        this.noteStore.updateUser(records[0]);
                        this.checkRemainingXHRs();
                    },
                    scope: this
                }
            }
        });

        this.log("Init with target "
            + (this.target !== null ? true : false) + " at "
            + this.position);

        this.signalementWindow = new Ext.Window({
            title: this.getText(record),
            width: 640,
            height: 380,
            closable: true,
            closeAction: "hide",
            autoScroll: true,
            items: [
                new Ext.FormPanel({
                    labelWidth: 100, // label settings here cascade unless overridden
                    frame: true,
                    bodyStyle: 'padding:5px 5px 0',
                    width: 640,
                    defaults: {width: 230},
                    defaultType: 'textfield',
                    store: this.noteStore,
                    items: [{
                        fieldLabel: 'Vous êtes',
                        name: 'name',
                        dataIndex:'login'
                    }, {
                        fieldLabel: 'Collectivité ',
                        name: 'collectivite'
                    }, {
                        fieldLabel: 'Votre adresse mail',
                        name: 'email',
                        vtype: 'email'

                    }, {
                        xtype: 'compositefield',
                        anchor: '-20',
                        msgTarget: 'side',
                        fieldLabel: 'Signalement sur la couche',
                        items: [
                            {
                                width: 200,
                                xtype: 'combo',
                                mode: 'local',
                                triggerAction: 'all',
                                forceSelection: true,
                                editable: false,
                                fieldLabel: 'Title',
                                name: 'title',
                                hiddenName: 'title',
                                displayField: 'name',
                                valueField: 'value',
                                store: new Ext.data.JsonStore({
                                    fields: ['name', 'value'],
                                    data: [
                                        {name: 'Mr', value: 'mr'},
                                        {name: 'Mrs', value: 'mrs'},
                                        {name: 'Miss', value: 'miss'}
                                    ]
                                })
                            }
                        ]
                    }, {
                            xtype: 'textfield',
                            fieldLabel: 'Objet',
                            height: 100,
                            width: 300,
                    }
                    ]

    initRecord: null,
    /**
     * Window containing the " note de signalement » - {Ext.Window}
     */
    signalementWindow: null,

    /**
     * Informations retrieved from addon server about " user »
     */
    userStore: null,
    test: null,

    /**
     * Information retrieved from addon server about "attachment configuration"
     */
    attachmentConfigurationStore: null,

    /**
     * Informations retrieved from addon server about " list of themas »
     */
    themasStore: null,

    themaStoreLoaded: false,

    /**
     * Information  retrieved from addon server about " list of layers »
     */
    layersStore: null,

    layersStoreLoaded: false,
    
    vectorLayer: null,

    /**
     * Data representing a Note de renseignement de signalement - {Object}
     */
    noteStore: new GEOR.data.NoteStore(),

    /**
     * api: config[encoding] ``String`` The encoding to set in the headers when
     * requesting the print service. Prevent character encoding issues,
     * especially when using IE. Default is retrieved from document charset or
     * characterSet if existing or ``UTF-8`` if not.
     */
    encoding: document.charset || document.characterSet || "UTF-8",

    init: function (record) {
        this.initRecord = record;

        if (this.target) {
            // create a button to be inserted in toolbar:
            this.components = this.target.insertButton(this.position, {
                xtype: 'button',
                tooltip: this.getTooltip(record),
                iconCls: "addon-signalement",
                handler: this._onCheckchange,
                scope: this,
                listeners: {
                    "afterrender": function () {
                        if (this.options.openToolbarOnLoad) { // ???
                            this._onCheckchange(this.item, true);
                        }
                    },
                    delay: 500,
                    scope: this
                }
            });
            this.target.doLayout();
        }

        this.userStore = this.buildUserStore();

        this.themasStore = this.buildThemasStore();

        this.layersStore = this.buildLayersStore();

        this.attachmentConfigurationStore = this.buildAttachmentConfigurationStore();

        // initialize note store
        this.noteStore.loadData([{
            "user": {},
            "task": {},
            "themas": [],
            "layers": [],
            "attachmentConfiguration": {}
        }]);

        this.remainingXHRs = 0;
        // preload themas
        this.themasStore.load();
        this.remainingXHRs += 1;

        // preload layers
        this.layersStore.load();
        this.remainingXHRs += 1;

        // preload attachmentConfiguration
        this.attachmentConfigurationStore.load();
        this.remainingXHRs += 1;

        this.log("Init done.");
    },

    buildUserStore: function () {
        return new Ext.data.JsonStore({
            id: "login",
            root: "",
            fields: [
                "login",
                "firstName",
                "lastName",
                "email",
                "organization"
            ],
            proxy: new Ext.data.HttpProxy({
                method: "GET",
                url: this.options.signalementURL + "user/me"
            }),
            listeners: {
                "load": {
                    fn: function (store, records) {
                        this.noteStore.updateUser(records[0].json);
                        this.fillForm();
                        this.checkRemainingXHRs();
                    },
                    scope: this
                }
            }
        });
    },

    buildAttachmentConfigurationStore: function () {
        return new Ext.data.JsonStore({
            id: "maxSize",
            root: "",
            fields: [
                "maxSize",
                "maxCount",
                "mimeTypes"
            ],
            proxy: new Ext.data.HttpProxy({
                method: "GET",
                url: this.options.signalementURL + "reporting/attachment/configuration"
            }),
            listeners: {
                "load": {
                    fn: function (store, records) {
                        this.noteStore.updateAttachmentConfiguration(records[0].json);
                        this.checkRemainingXHRs();
                    },
                    scope: this
                }
            }
        });
    },


    buildThemasStore: function () {
        return new Ext.data.JsonStore({
            id: "name",
            root: "",
            fields: [
                "contextType",
                "geographicType",
                "name",
                "label",
            ],
            baseParams: {
                contextType: 'THEMA'
            },
            proxy: new Ext.data.HttpProxy({
                method: "GET",
                url: this.options.signalementURL + this.options.contextType
            }),
            listeners: {
                "load": {
                    fn: function (store, records) {
                        var list = [];
                        Ext.each(records, function (r) {
                            list.push(r.data);
                        });
                        this.noteStore.updateThemas(list);
                        this.themaStoreLoaded = true;
                        this.checkRemainingXHRs();
                    },
                    scope: this
                }
            }
        });
    },

    buildLayersStore: function () {
        return new Ext.data.JsonStore({
            id: "name",
            root: "",
            fields: [
                "contextType",
                "geographicType",
                "name",
                "label",
            ],
            baseParams: {
                contextType: 'LAYER'
            },
            proxy: new Ext.data.HttpProxy({
                method: "GET",
                url: this.options.signalementURL + this.options.contextType
            }),
            listeners: {
                "load": {
                    fn: function (store, records) {
                        var list = [];
                        Ext.each(records, function (r) {
                            list.push(r.data);
                        });
                        this.noteStore.updateLayers(list);
                        this.layerStoreLoaded = true;
                        this.checkRemainingXHRs();
                    },
                    scope: this
                }
            }
        });
    },

    buildAttachmentPanel: function() {
    	return new Ext.grid.GridPanel({
    		id: "attachmentPanel",
    	    store: new Ext.data.JsonStore({
    	        autoDestroy: true,
    	        id: "attachmentStore",
    	        root: ''
    	    }),
    	    colModel: new Ext.grid.ColumnModel({
    	        defaults: {
    	            sortable: false
    	        },
    	        columns: [
    	            {id: 'id', header: 'Nom', dataIndex: 'name'},
    	            {
    	            	xtype: 'actioncolumn',
    	            	items: [
    	                    {
    	                        icon   : 'img/delete.gif',                // Use a URL in the icon config
    	                        tooltip: this.tr('signalement.attachment.delete'),
    	                        handler: function(grid, rowIndex, colIndex) {
    	                            var rec = store.getAt(rowIndex);
    	                            alert("Sell " + rec);
    	                        }
    	                    }
    	                ]
    	            }
    	        ]
    	    }),
    	    iconCls: 'attachment-grid'
    	});
    },
    
    buildForm: function() {
    	var themas = this.noteStore.getThemas();
    	var addon = this;
    	
    	var addon = this;
        var layerFeature;
        
        var drawActionControl = function (typeGeom) {

            addon.vectorLayer = new OpenLayers.Layer.Vector("Signalement");

            //add vector layer in map
            addon.map.addLayer(addon.vectorLayer);

            if (typeGeom == 'POLYGON') {
                layerFeature = new OpenLayers.Control.DrawFeature(
                    addon.vectorLayer, OpenLayers.Handler.Polygon);
            } else if (typeGeom == 'LIGNE') {
                layerFeature = new OpenLayers.Control.DrawFeature(
                	addon.vectorLayer, OpenLayers.Handler.Path);
            } else if (typeGeom == 'POINT') {
                layerFeature = new OpenLayers.Control.DrawFeature(
                	addon.vectorLayer, OpenLayers.Handler.Point);
            }

            addon.map.addControl(layerFeature);
            layerFeature.activate();

            addon.vectorLayer.events.register('featureadded', layerFeature, onAdded);

        }

        function onAdded(evt) {
            layerFeature.deactivate();

            //transformer les points en projection EPSG : 4326
            var sourceSRS = addon.map.getProjection();
            var destSRS = new OpenLayers.Projection("EPSG:4326")
            var vector = evt.feature.geometry.transform(sourceSRS, destSRS);

            var list = vector.getVertices();
            var listLocalisation = [];
            for (var i = 0; i < list.length; i++) {
                listLocalisation.push({'x': list[i].x, 'y': list[i].y});
            }
            console.log('listLocalisation:' + listLocalisation);
            addon.noteStore.updateLocalisation(listLocalisation);
            Ext.getCmp('createButton').setDisabled(false);

        }
    	
    	return new Ext.FormPanel({
            id: 'form-panel',
            labelWidth: 100,
            frame: true,
            bodyStyle: 'padding:5px 5px 0',
            width: 600,
            items: [
                {
                    xtype: 'fieldset',
                    title: this.tr('signalement.user'),
                    collapsible: false,
                    width: 500,
                    store: 'type',
                    items: [
                        {
                            xtype: 'textfield',
                            fieldLabel: this.tr('signalement.login'),
                            name: 'login',
                            id: 'login',
                            readOnly: true
                        },
                        {
                            xtype: 'textfield',
                            fieldLabel: this.tr('signalement.organization'),
                            name: 'organization',
                            id: 'organization',
                            readOnly: true
                        },
                        {
                            xtype: 'textfield',
                            fieldLabel: this.tr('signalement.email'),
                            name: 'email',
                            vtype: 'email',
                            id: 'email',
                            readOnly: true

                        }
                    ]
                },
                {
                    xtype: 'fieldset',
                    title: this.tr('signalement.reporting.thema'),
                    collapsible: false,
                    width: 500,
                    items: [{
                        xtype: 'compositefield',
                        anchor: '-20',
                        msgTarget: 'side',
                        items: [
                            {
                                xtype: 'combo',
                                id: 'combo',
                                width: 200,
                                queryMode: 'local',
                                forceSelection: true,
                                displayField: 'label',
                                valueField: 'name',
                                store: this.themasStore,
                                value: themas[0].name,
                                listeners: {
                                    select: function (combo, record) {
                                        addon.noteStore.getTask().asset.contextDescription = record.data;
                                        addon.noteStore.getTask().asset.geographicType = record.data.geographicType;
                                        console.log("task: " + addon.noteStore.getTask());
                                        if (addon.vectorLayer != undefined) {
                                            addon.vectorLayer.destroyFeatures();
                                        }
                                        Ext.getCmp('createButton').setDisabled(true);
                                    }
                                }
                            }
                        ]
                    }]
                },
                {
                    xtype: 'fieldset',
                    title: "Objet",
                    id: "object",
                    collapsible: false,
                    width: 500,
                    items: [
                        {
                            xtype: 'textfield',
                            id: 'objet',
                            height: 100,
                            width: 300
                    }]
                }, 
                {
                    xtype: 'fieldset',
                    title: this.tr('signalement.attachment.files'),
                    collapsible: false,
                    width: 500,
                    items: [
                    	{
                            xtype: 'fileuploadfield',
                            id: 'form-file-field',
                            emptyText: this.tr('signalement.attachment.select'),
                            fieldLabel: this.tr('signalement.attachment.add'),
                            name: 'photo-path',
                            buttonText: '',
                            buttonCfg: {
                                iconCls: 'upload-icon'
                            },
                            listeners: {
                            	"fileselected": {
                                    fn: function (fileuploadfield, v) {
                                    	this.uploadAttachment(fileuploadfield, v);
                                    },
                                    scope: this
                                }
                            }
                        },
                        this.buildAttachmentPanel()
                    ]
                },
                {
                    xtype: 'fieldset',
                    title: this.tr('signalement.localization'),
                    collapsible: false,
                    width: 500,
                    items: [
                        {
                            xtype: 'button',
                            id: 'drawBtn',
                            iconCls: 'draw-icon',
                            scope: this,
                            handler: function () {
                                if (addon.vectorLayer != undefined) {
                                    addon.vectorLayer.destroyFeatures();
                                    addon.map.removeLayer(addon.vectorLayer);
                                }
                                drawActionControl(this.noteStore.getTask().asset.geographicType);
                                Ext.getCmp('createButton').setDisabled(true);

                            }
                        },
                        {
                            xtype: 'displayfield',
                            value: this.tr('signalement.localization.tips')

                        }
                   ]
                }
            ]
        });
    },

    buildSignalementWindow: function () {
        var themas = this.noteStore.getThemas();

        var form = this.buildForm();

        this.signalementWindow = new Ext.Window({
            title: this.getText(this.initRecord),
            width: 600,
            autoScroll: false,
            items: [form],
            buttons: [
            	{
	                id: 'createButton',
	                text: this.tr('signalement.create'),
	                disabled: true,
	                handler: function () {
	                    this.createTask();
	                },
	                scope: this
            	},
                {
                    id: 'closeButton',
                    text: this.tr("signalement.close"),
                    handler: function () {
                        if (addon.vectorLayer != undefined) {
                            addon.vectorLayer.destroyFeatures();
                            addon.map.removeLayer(addon.vectorLayer);
                        }
                        this.closeSignalementWindow();
                    },
                    scope: this
                }
            ],
            listeners: {
                hide: function () {
                    this.item && this.item.setChecked(false);
                    this.components && this.components.toggle(false);
                },
                scope: this
            }
        });

        this.fillForm();

    },

    checkRemainingXHRs: function () {
        this.remainingXHRs -= 1;
        if (this.remainingXHRs == 0) {
            //this.mask.hide();
        }
    },

    fillForm: function () {
        if (Ext.ComponentMgr.get('login') != null) {
            Ext.ComponentMgr.get('login').setValue(this.noteStore.getAt(0).get("user").login);
            Ext.ComponentMgr.get('organization').setValue(this.noteStore.getAt(0).get("user").organization);
            Ext.ComponentMgr.get('email').setValue(this.noteStore.getAt(0).get("user").email);
        }
    },

    showSignalementWindow: function () {
        this.userStore.load();
        this.remainingXHRs += 1;

        if (this.themaStoreLoaded /*remainingXHRs==0*/) {
            this.createDraftTask();
        }

    },

    closeSignalementWindow: function () {
        if (this.signalementWindow != null) {
            Ext.getCmp('closeButton').setDisabled(true);
            Ext.getCmp('createButton').setDisabled(true);
            this.deleteDraftTask();
        }
    },

    createTask: function () {
        var task = this.noteStore.getTask();
        task.asset.description = Ext.getCmp('objet').getValue();

        Ext.Ajax.request({
            url: this.options.signalementURL + "task/start",
            method: 'POST',
            jsonData: task,
            headers: {
                "Content-Type": "application/json; charset=" + this.encoding
            },
            success: function (response) {
                this.log("response: ", response);

                //this.fireEvent("success", this, response);
            },
            failure: function (response) {
                this.log("response: ", response);
                //this.fireEvent("exception", this, response);
            },
            scope: this
        });
    },
    
    createDraftTask: function () {
        var themas = this.noteStore.getThemas();
        var params = {
            "description": "",
            "contextDescription": themas[0]
        };
        Ext.Ajax.request({
            url: this.options.signalementURL + "task/draft",
            method: 'POST',
            jsonData: params,
            headers: {
                "Content-Type": "application/json; charset=" + this.encoding
            },
            success: function (response) {
                this.log("task: ", response);

                var task = Ext.util.JSON.decode(response.responseText)
                this.noteStore.updateTask(task);

                // build window
                this.buildSignalementWindow();

                // display window
                this.signalementWindow.show();

            },
            failure: function (response) {
                this.log("task bad-response: ", response);
            },
            scope: this
        });
    },

    deleteDraftTask: function () {
        var task = this.noteStore.getTask();
        var params = {};
        Ext.Ajax.request({
            url: this.options.signalementURL + "task/cancel/" + task.asset.uuid,
            method: 'DELETE',
            jsonData: params,
            headers: {
                "Content-Type": "application/json; charset=" + this.encoding
            },
            success: function (response) {
                var task = Ext.util.JSON.decode(response.responseText)
                this.noteStore.updateTask({});
                this.signalementWindow.hide();
                this.signalementWindow.destroy();
                this.signalementWindow = null;
            },
            failure: function (response) {
                this.log("task bad-response: ", response);
            },
            scope: this
        });
    },
    
    deleteAttachment: function (attachment) {
        var task = this.noteStore.getTask();
        Ext.Ajax.request({
            url: this.options.signalementURL + "reporting/" + task.asset.uuid + "/delete/" + attachment.id,
            method: 'DELETE',
            headers: {
                "Content-Type": "application/json; charset=" + this.encoding
            },
            success: function (response) {
                task.attachments.remove(attachment);
                Ext.getCmp('attachmentPanel').getStore().remove(attachment);
            },
            failure: function (response) {
                this.log("task bad-response: ", response);
            },
            scope: this
        });
    },
    
    uploadAttachment: function(fileField, value) {
    	var task = this.noteStore.getTask();
    	if( !task.asset.attachments){
    		task.asset.attachments = [];
    	}
    	var file = fileField.fileInput.dom.files[0];

    	if (file === undefined || !(file instanceof File)) {
    		return;
    	}
    	if( file.size > this.noteStore.getAttachmentConfiguration().maxSize){
    		// todo message erreur
    		return;
    	}
    	if( task.asset.attachments.length+1 > this.noteStore.getAttachmentConfiguration().maxCount){
    		// todo message erreur
    		return;
    	}
    	if( this.noteStore.getAttachmentConfiguration().mimeTypes.includes(file.type) == false){
    		// todo message erreur
    		return;
    	}
    	
    	var form = document.getElementById('signalement-form-id');
    	
    	var formData = new FormData();
    	var addon = this;
    	formData.set("file", file , file.name);
    	var request = new XMLHttpRequest();
    	request.onload = function(response, a) {
    		console.log(response, a);
    		if (request.status == 200) {
    			var attachment = Ext.util.JSON.decode(request.responseText)
    			task.asset.attachments.push(attachment);
    			Ext.getCmp('attachmentPanel').getStore().add(attachment);
    		}else {
    			// todo
    		}
    			
    	}
    	request.onerror = function(response, b) {
    		// todo 
    		console.log(response, b);
    	}
        request.open('POST', this.options.signalementURL + "reporting/" + task.asset.uuid + "/upload");
        request.send(formData);
    },

    destroy: function () {
        this.map.removeLayer(this.vectorLayer);
        this.userStore.destroy();
        this.noteStore.destroy();
        this.listThemaStore.destroy();
        GEOR.Addons.Base.prototype.destroy.call(this);
    },

    /**
     * Le log de base
     */
    log: function (message) {
        if (this.options.log === "true") {
            console.log(message);
        }
    },

    /**
     * @function tr
     *
     * Translate string
     */
    tr: function (a) {
        return OpenLayers.i18n(a);
    },

    /**
     * Method: _onCheckchange Callback on checkbox state changed
     */
    _onCheckchange: function (item, checked) {
        this.log("Change:" + item);
        if (checked) {
            // appel
            this.showSignalementWindow();
        }
    }
});
