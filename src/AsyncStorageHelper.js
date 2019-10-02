import { NativeModules, Platform } from 'react-native'

const RNAsyncStorageHelper = NativeModules.RNPhpfoxAsyncStorageHelper

function setup () {
  if (Platform.OS === 'android') {
    return
  }
  RNAsyncStorageHelper.setup()
}

export const AsyncStorageHelper = {
  setup
}
