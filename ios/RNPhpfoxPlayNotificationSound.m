#import "RNPhpfoxPlayNotificationSound.h"
#import <AudioToolbox/AudioToolbox.h>
@implementation RNPhpfoxPlayNotificationSound

RCT_EXPORT_MODULE()

SystemSoundID soundID = 0;

RCT_EXPORT_METHOD(playTypingSound)
{
      NSURL *url = [[NSBundle mainBundle] URLForResource:@"typing_sound.mp3" withExtension:nil];
    
      AudioServicesDisposeSystemSoundID(soundID);
      //Register the sound to the system
      AudioServicesCreateSystemSoundID((__bridge CFURLRef)url, &soundID);
      AudioServicesPlaySystemSound(soundID);
      AudioServicesPlaySystemSoundWithCompletion(soundID, ^{
          AudioServicesRemoveSystemSoundCompletion(soundID);
          AudioServicesDisposeSystemSoundID(soundID);
      });
}


@end
