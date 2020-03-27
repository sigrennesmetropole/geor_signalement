/*global
 Ext, GeoExt, OpenLayers, GEOR
 */
Ext.namespace("GEOR.Addons", "GEOR.data");

/**
 *  GEOR.data.NoteStore - JsonStore representing a « note de renseignement signalement
 *
 */
(function() {
    var NoteStore = Ext.extend(Ext.data.JsonStore, {
        constructor: function(config) {
            config = Ext.apply({
                root: "",
                fields: [ "login",
                    "firstName",
                    "lastName",
                    "email",
                    "organisation"
                ]
                //Add proxy configuration here if we want to upload Note data to server
            }, config);

            NoteStore.superclass.constructor.call(this, config);
        },
        updateUser: function(user) {

            var noteRecord = this.getAt(0).copy();
            //noteRecord.set("login", 'test');
            noteRecord.set("login",  user.get("login"));
            noteRecord.set("firstName", 'test');
            noteRecord.set("lastName", 'test');
            noteRecord.set("email", 'test');
            noteRecord.set("organisation", 'test');
            this.add([noteRecord]);
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

                })

            ],
            buttons: [{
                //TODO tr
                text: "Créer",
                handler: function () {
                    var params;

                    params = {
                        attributes: {}
                    };

                    Ext.Ajax.request({
                        url: "url",
                        method: 'POST',
                        jsonData: (new OpenLayers.Format.JSON()).write(params),
                        headers: {
                            "Content-Type": "application/json; charset=" + this.encoding
                        },
                        success: function (response) {
                            this.fireEvent("success", this, response);
                        },
                        failure: function (response) {
                            this.fireEvent("exception", this, response);
                        },
                        params: this.baseParams,
                        scope: this
                    });
                },
                scope: this
            },
                {
                    text: "Fermer",
                    handler: function () {
                        this.window.hide();
                    },
                    scope: this
                }],
            listeners: {
                hide: function () {
                    this.item && this.item.setChecked(false);
                    this.components && this.components.toggle(false);
                },
                scope: this
            }
        });

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

        this.log("Init done.");

    },

    showSignalementWindow:function(){
        this.remainingXHRs -= 1;
        if (this.remainingXHRs == 0) {
            //this.parcelleWindow.getFooterToolbar().getComponent('print').enable();
            this.mask.hide();
        }

        this.userStore.load();

        this.signalementWindow.show();


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
        // TODO 
        // si la layer est associé à un contexte signalement on ouvre la fenêtre pour cette couche
        // dans le cas contraire soit on affiche un message indiquant que la couche ne supporte pas le signalement
        // soit on ouvre la fenêtre de signalement par thème
        this.showSignalementWindow();
    },
    
    /**
     * @function initActionMenuDate
     * 
     * Initialize fields used by tree Actions menu to create MenuItem
     */
    initActionMenuDate: function() {
    	this.iconCls ='addon-signalement';
        this.title =  this.getText(record);
        this.qtip = this.getTooltip(record);
    }

    destroy: function () {
        this.window.hide();
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
