import { NativeModules, Platform } from 'react-native'

const RNPhpfoxImageFetcher = NativeModules.RNPhpfoxImageFetcher

async function getImageUrl(uri) {
  if (Platform.OS === 'android') {
    return
  }

  return await RNPhpfoxImageFetcher.saveToDocumentsFolder(uri)
}

export const ImageFetcher = { getImageUrl }

