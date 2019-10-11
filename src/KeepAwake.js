import { NativeModules } from 'react-native'

const activate = () => NativeModules.RNPhpfoxKeepAwake.activate()

const deactivate = () => NativeModules.RNPhpfoxKeepAwake.deactivate()

export const KeepAwake = {
  activate,
  deactivate
}
