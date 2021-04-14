import { NativeModules, NativeEventEmitter } from 'react-native'

const BatchedBridge = require('react-native/Libraries/BatchedBridge/BatchedBridge')

const CallStateUpdateAction = {
  callStateUpdated(state) {
    console.log('callStateUpdated')
    this.callback && this.callback(state)
  }
}

BatchedBridge.registerCallableModule('RNPhpfoxCallStateUpdateAction', CallStateUpdateAction)

const RNPhpfoxCallState = NativeModules.RNPhpfoxCallState

export class CallDetector {
  subscription = undefined

  callback = undefined

  constructor(callback) {
    this.callback = callback

    if (Platform.OS === 'ios') {
      if (RNPhpfoxCallState) {
        console.log('Have RNPhpfoxCallState')
        RNPhpfoxCallState.startListener()
        this.subscription = new NativeEventEmitter(RNPhpfoxCallState)
        this.subscription.addListener('PhoneCallStateUpdate', callback);
      }
    } else {
      if (RNPhpfoxCallState) {
        console.log('Have RNPhpfoxCallState')
        RNPhpfoxCallState.startListener();
        CallStateUpdateAction.callback = callback
      }
    }
  }

  dispose() {
    if (RNPhpfoxCallState) {
      RNPhpfoxCallState.stopListener()
    }

    CallStateUpdateAction.callback = undefined

    if (this.subscription) {
      this.subscription.removeAllListeners('PhoneCallStateUpdate');
      this.subscription = undefined
    }
  }
}

