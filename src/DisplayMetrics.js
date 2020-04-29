import { NativeModules, Platform } from 'react-native'

export function get(dim) {
  if (Platform.OS !== 'android') {
    return 0
  } else {
    try {
      if (!NativeModules.RNPhpfoxDislayMetric) {
        throw "DisplayMetrics is not defined"
      }

      return NativeModules.RNPhpfoxDislayMetric[dim]
    } catch (e) {
      console.log(e)
      return 0
    }
  }
}


export function getSoftMenuBarHeight() {
  return get('SOFT_MENU_BAR_HEIGHT')
}

export function isSoftMenuBarEnabled() {
  return get('SOFT_MENU_BAR_ENABLED')
}

export const DisplayMetrics = {
  getSoftMenuBarHeight,
  isSoftMenuBarEnabled
}