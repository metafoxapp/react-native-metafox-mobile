import { NativeModules } from 'react-native'

const playTypingSound = () => NativeModules.RNPhpfoxPlayNotificationSound.playTypingSound()

export const NotificationSound = {
  playTypingSound
}