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
            this.getTask().asset.localisation = points;
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
     * fields used to build tree Action Menu item
     */
    title : null,
    iconCls: null,
    qtip: null,

    /**
     * field used to store current layer record
     */
    layerRecord: null,

    initRecord: null,
    /**
     * Window containing the " note de signalement » - {Ext.Window}
     */
    signalementWindow: null,

    /**
     * Information retrieved from addon server about "attachment configuration"
     */
    attachmentConfigurationStore: null,

    /**
     * Information about signalement if it's used for layer or thema
     */
    reportThema: true,
    
    /**
     * Informations retrieved from addon server about « user »
     */
    userStore : null,
    
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

        this.initActionMenuDate();

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
                "label"
            ],
            baseParams: {
                contextType: 'THEMA'
            },
            proxy: new Ext.data.HttpProxy({
                method: "GET",
                url: this.options.signalementURL + "reporting/contextDescription/search"
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
                "label"
            ],
            baseParams: {
                contextType: 'LAYER'
            },
            proxy: new Ext.data.HttpProxy({
                method: "GET",
                url: this.options.signalementURL + "reporting/contextDescription/search"
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
    	    store:new Ext.data.JsonStore({
                autoDestroy: true,
                fields: [
                    {name: 'id'},
                    {name: 'name'},
                    {name: 'mimeType'}
                ]

            }) ,
    	    colModel: new Ext.grid.ColumnModel({
    	        defaults: {
    	            sortable: false
    	        },
    	        columns: [
    	            {   id: 'id',
                        header: 'Name',
                        dataIndex: 'name'},
    	            {
    	            	xtype: 'actioncolumn',
    	            	items: [
    	                    {
                                iconCls: 'delete-icon',
    	                        tooltip: this.tr('signalement.attachment.delete'),
    	                        handler: function(grid, rowIndex) {
    	                            var attachement = this.noteStore.getTask().asset.attachments[rowIndex];
                                    this.deleteAttachment(attachement,rowIndex);
    	                        },
                                scope: this
    	                    }
    	                ]
    	            }
    	        ]
    	    }),
    	    iconCls: 'attachment-grid',
            stripeRows: true,
            autoExpandColumn: 'id',
            height: 100,
            width: 400,
            //title: 'Array Grid',
            // config options for stateful behavior
            stateful: true,
            stateId: 'grid'
    	});
    },

    buildForm: function() {
        var storeCombo, valueCombo, titleCombo, iconGeom, nbrCharLimit = 1000;
        // teste si le signalement est pour une couche ou thématique
        if(this.reportThema == true){
            storeCombo= this.themasStore;
            valueCombo= this.noteStore.getThemas()[0].name;
            titleCombo=  this.tr('signalement.reporting.thema');
        }else{
            storeCombo= this.layersStore;
            valueCombo= this.noteStore.getTask().asset.contextDescription.name;
            titleCombo=  this.tr('signalement.reporting.layer');
        }
        iconGeom= this.noteStore.getTask().asset.geographicType;


        var addon = this;
    	var layerFeature;

        var drawActionControl = function (typeGeom) {

            addon.vectorLayer = new OpenLayers.Layer.Vector(this.tr('signalement.layer.name'));

            //add vector layer in map
            addon.map.addLayer(addon.vectorLayer);

            if (typeGeom == 'POLYGON') {
                layerFeature = new OpenLayers.Control.DrawFeature(
                    addon.vectorLayer, OpenLayers.Handler.Polygon);
            } else if (typeGeom == 'LINE') {
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
                    title:titleCombo,
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
                                store: storeCombo,
                                value: valueCombo,
                                listeners: {
                                    select: function (combo, record) {
                                        addon.noteStore.getTask().asset.contextDescription = record.data;
                                        addon.noteStore.getTask().asset.geographicType = record.data.geographicType;
                                        //changer l'icon pour dessiner une feature en fonction de la geometrie
                                        Ext.getCmp('drawBtn').setIconClass(record.data.geographicType);

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
                    title: this.tr("signalement.description"),
                    id: "object",
                    collapsible: false,
                    width: 500,
                    items: [
                        {
                            xtype: 'textarea',
                            id: 'objet',
                            height: 100,
                            width: 300,
                            maxLength: nbrCharLimit,
                            enableKeyEvents : true,
                            listeners: {
                                keyup: function(){
                                    var nbrChar = nbrCharLimit - Ext.getCmp('objet').getValue().length;
                                    Ext.get('numChar').update(nbrChar);
                                }
                            }
                        },
                        {
                            xtype: 'displayfield',
                            id:'labelObjet',
                            value: 'nombre de caractères restants: <span id="numChar">'+ nbrCharLimit +'</span>',
                            cls : 'labelFile'
                        }
                        ]
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
                            name: 'file',
                            buttonText: '',
                            buttonCfg: {
                                iconCls: 'upload-icon'
                            },
                            listeners: {
                            	"fileselected": {
                                    fn: function (fileuploadfield, v) {
                                    	this.uploadAttachment(fileuploadfield, v);
                                    	//afficher que le nom fichier ajouter sans le fakepath
                                        var fileName = v.replace(/C:\\fakepath\\/g, '');
                                        fileuploadfield.setRawValue(fileName);
                                    },
                                    scope: this
                                }
                            }
                        },
                        {
                            xtype: 'displayfield',
                            value: this.tr('signalement.fileUpload.info'),
                            cls : 'labelFile',
                        },
                        this.buildAttachmentPanel()
                    ]
                },
                {
                    xtype: 'fieldset',
                    title: this.tr('signalement.localization'),
                    collapsible: false,
                    width: 500,
                    layout: {
                        type: 'hbox',
                        align: 'middle'
                    },
                    items: [
                        {
                            xtype: 'button',
                            id: 'drawBtn',
                            iconCls: iconGeom,
                            scope: this,
                            handler: function () {
                                this.removeLayer(addon);
                                drawActionControl(this.noteStore.getTask().asset.geographicType);
                                Ext.getCmp('createButton').setDisabled(true);

                            }
                        },
                        {
                            xtype: 'displayfield',
                            value: this.tr('signalement.localization.tips'),
                            cls : 'labelButtonGeom',
                        }
                   ]
                }
            ]
        });
    },

    buildSignalementWindow: function () {

        var form = this.buildForm();
        //Dans le cas d'un signalement d'une couche le button est grisé
        if(this.reportThema == false){
            Ext.getCmp('combo').setDisabled(true);
        }


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
                        var addon =this;
                        // message d'alert pour confirmer la fermeture de la fenetre
                        Ext.MessageBox.confirm(this.tr("signalement.msgBox.title"), this.tr("signalement.msgBox.info"), function (btn) {
                            if(btn =='yes'){
                                addon.removeLayer(addon);
                                addon.closeSignalementWindow();
                            }
                        });

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

    showSignalementWindow: function (layer) {
        this.userStore.load();
        this.remainingXHRs += 1;
        var params;
        // parametre pour signalement par couche ou par theme
        if (layer == undefined) {
            var themas = this.noteStore.getThemas();
            params= {
                "description": "",
                "contextDescription": themas[0]
            };
        }else{
            //var layers = this.noteStore.getLayers();
            params= {
                "description": "",
                "contextDescription": layer
            };
        }
        this.createDraftTask(params);


    },

    closeSignalementWindow: function () {
        if (this.signalementWindow != null) {
            Ext.getCmp('closeButton').setDisabled(true);
            Ext.getCmp('createButton').setDisabled(true);
            this.deleteDraftTask();
        }
    },

    closeWindow: function () {
        if (this.signalementWindow != null) {
            Ext.getCmp('closeButton').setDisabled(true);
            Ext.getCmp('createButton').setDisabled(true);
            this.noteStore.updateTask({});
            this.signalementWindow.hide();
            this.signalementWindow.destroy();
            this.signalementWindow = null;
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
                this.removeLayer(this);
                this.closeWindow();
                Ext.Msg.show({
                    msg: this.tr('signalement.task.create'),
                    buttons: Ext.Msg.OK
                });
            },
            failure: function (response) {
                Ext.Msg.show({
                    msg: this.tr('signalement.task.error')
                });
                this.log("response: ", response);
            },
            scope: this
        });
    },

    createDraftTask: function (params) {

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
        Ext.Ajax.request({
            url: this.options.signalementURL + "task/cancel/" + task.asset.uuid,
            method: 'DELETE',
            jsonData: task,
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

    deleteAttachment: function (attachment,index) {
        var task = this.noteStore.getTask();
        Ext.Ajax.request({
            url: this.options.signalementURL + "reporting/" + task.asset.uuid + "/delete/" + attachment.id,
            method: 'DELETE',
            headers: {
                "Content-Type": "application/json; charset=" + this.encoding
            },
            success: function (response) {
                //supprimer l'element de attachements
                task.asset.attachments.splice(index,1);
                Ext.getCmp('attachmentPanel').getStore().loadData(task.asset.attachments);
                },
            failure: function (response) {
                this.log("task bad-response: ", response);
            },
            scope: this
        });
    },

    uploadAttachment: function(fileField, value,data) {
    	var task = this.noteStore.getTask();
    	if( !task.asset.attachments){
    		task.asset.attachments = [];
    	}
    	var file = fileField.fileInput.dom.files[0];

    	if (file === undefined || !(file instanceof File)) {
    		return;
    	}
    	if( file.size > this.noteStore.getAttachmentConfiguration().maxSize){
            Ext.Msg.show({
                title:this.tr('signalement.attachment.file'),
                msg: this.tr('signalement.attachment.size') + this.noteStore.getAttachmentConfiguration().maxSize
            });
    		return;
    	}
    	if( task.asset.attachments.length+1 > this.noteStore.getAttachmentConfiguration().maxCount){
            Ext.Msg.show({
                title:this.tr('signalement.attachment.file'),
                msg: this.tr('signalement.attachment.length')
            });
    		return;
    	}
    	if( this.noteStore.getAttachmentConfiguration().mimeTypes.includes(file.type) == false){
            Ext.Msg.show({
                title:this.tr('signalement.attachment.file'),
                msg: this.tr('signalement.attachment.typeFile')
            });
    		return;
    	}

    	var form = document.getElementById('signalement-form-id');

    	var formData = new FormData();
    	var addon = this;
    	formData.set("file", file , file.name);
    	
    	var request = new XMLHttpRequest();
    	request.onload = function(response, a) {
    		if (request.status == 200) {
    			var attachment = Ext.util.JSON.decode(request.responseText)
    			task.asset.attachments.push(attachment);
    			Ext.getCmp('attachmentPanel').getStore().loadData(task.asset.attachments);

            }else {
                console.log("upload file", request.status);
    		}

    	}
    	request.onerror = function(response, b) {
            Ext.Msg.show({
                msg: this.tr('signalement.attachment.error')
            });
            console.log(response, b);
    	}
    	
    	var originalUrl = this.options.signalementURL + "reporting/" + task.asset.uuid + "/upload"; 
    	var url = encodeURIComponent(originalUrl);
    	var proxyHost = OpenLayers.ProxyHost;
    	if( proxyHost !== "") {
    		var i = document.URL.indexOf(document.domain);
    		var j = document.URL.indexOf("/",i);
    		var prefix = document.URL.substring(0,j);
    		request.open('POST', prefix + proxyHost + url);
    	} else {
    		request.open('POST', originalUrl);
    	}
        request.send(formData);
        
    },

    /**
     * @function layerTreeHandler
     *
     * Handler for the layer tree Actions menu.
     *
     * scope is set for having the addons as this
     *
     * @param menuitem - menuitem which will receive the handler
     * @param event - event which trigger the action
     * @param layerRecord - layerRecord on which operate
     */
    layerTreeHandler: function(menuitem, event, layerRecord) {
        if (this.active) {
            return;
        }
        // set layer record:
        this.layerRecord = layerRecord;

        // on cherche si le layer est associé à une couche
        var layerReport;
        this.noteStore.getLayers().forEach(function (layer) {
            if(layer.name == layerRecord.data.name){
                layerReport=layer;
            }
        })

        // si la layer est associé à un contexte signalement on ouvre la fenêtre pour cette couche
        // dans le cas contraire  on affiche un message indiquant que la couche ne supporte pas le signalement
        if(layerReport) {
            this.reportThema = false;
            this.showSignalementWindow(layerReport);
        }else{
            Ext.Msg.show({
                msg: this.tr('signalement.localization.layer')
            });
        }
    },

    /**
     * @function initActionMenuDate
     *
     * Initialize fields used by tree Actions menu to create MenuItem
     */
    initActionMenuDate: function() {
    	this.iconCls ='addon-signalement';
        this.title =  this.getText(this.initRecord);
        this.qtip = this.getTooltip(this.initRecord);
    },

    removeLayer: function( addon ){
        if (addon.vectorLayer != undefined ) {
            addon.map.layers.map(function(layer) {
                if(layer.name == this.tr('signalement.layer.name')){
                    addon.map.removeLayer(layer);
                }
            } )
        }
    },

    destroy: function () {
        this.userStore.destroy();
        this.noteStore.destroy();
        this.themasStore.destroy();
        this.layersStore.destroy();
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
            // signalement par thème
            this.reportThema=true;
            this.showSignalementWindow();
        }
    }
});
