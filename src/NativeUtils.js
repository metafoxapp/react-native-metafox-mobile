const ModuleUtils = require('react-native').NativeModules.RNPhpfoxMobile

function setWindowColor(color) {
  ModuleUtils.setWindowColor(color)
}

export const NativeUtils = {
  setWindowColor
}
