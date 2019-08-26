const RNAsyncStorageHelper = require('react-native').NativeModules.RNPhpfoxAsyncStorageHelper

function setup () {
  RNAsyncStorageHelper.setup()
}

export const AsyncStorageHelper = {
  setup
}
