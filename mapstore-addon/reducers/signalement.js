import {actions} from '../actions/signalement';
const assign = require('object-assign');

const initialState = {
		attachmentConfiguration: null,
		themas: null,
		layers: null
};

export default function onlineStoreApp(state = initialState, action) {
    switch(action.type) {
        case actions.ADD_TO_CART:
            return Object.assign({}, state, { items : [...state.items, action.payload]});
        case actions.UPDATE_CART:
            return Object.assign({}, state, {
                items: state.items.map(item => {
                    return item.id === action.payload.item.id ? Object.assign({}, item, {
                        quantity: action.payload.quantity
                    } ) :  item;
                }) 
            });
        case actions.REMOVE_FROM_CART:
            return Object.assign({}, state, {
                items: state.items.filter(item => {
                    return item.id !== action.payload.item.id;
                })
            });
        case actions.SAVE_CART:
            saveToLocalStorage(action.payload.items);
            return Object.assign({}, state, {items: action.payload.items});
        case actions.RESET_CART:
            saveToLocalStorage([]);
            return Object.assign({}, state, {items: []});
        default:
            return state;
    }
}