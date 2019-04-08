const RNImageCompresser = require('react-native').NativeModules.RNPhpfoxImageCompresser

async function compress (options) {
  return new Promise((resolve, reject) => {
    RNImageCompresser.compressImage(options, error => {
      reject({ error })
    }, (data) => {
      resolve(data)
    })
  })
}

export const ImageCompresser = {
  compress
}
