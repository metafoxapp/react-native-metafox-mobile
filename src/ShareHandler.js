const RNShareHandler = require('react-native').NativeModules.RNPhpfoxShareHandlerModule

async function getData () {
  return await RNShareHandler.getData()
}

export const ShareHandler = {
  getData
}
