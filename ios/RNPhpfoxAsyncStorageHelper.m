#import "RNPhpfoxAsyncStorageHelper.h"
#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import <React/RCTBridge.h>
#import <React/RCTLog.h>
#import <RNCAsyncStorage/RNCAsyncStorage.h>
#import <CommonCrypto/CommonDigest.h>

@implementation RNPhpfoxAsyncStorageHelper

RCT_EXPORT_MODULE();

@synthesize bridge = _bridge;

// MARK: React Native methods

RCT_EXPORT_METHOD(setup)
{
    RNCAsyncStorage *asyncStorage = [[self bridge] moduleForClass:[RNCAsyncStorage class]];
    asyncStorage.delegate = self;
}

// MARK: RNCAsyncStorageDelegate protocol

- (void)allKeys:(RNCAsyncStorageResultCallback)block
{

}

- (void)mergeValues:(NSArray<NSString *> *)values
            forKeys:(NSArray<NSString *> *)keys
         completion:(RNCAsyncStorageResultCallback)block
{

}

- (void)removeAllValues:(RNCAsyncStorageCompletion)block
{

}

- (void)removeValuesForKeys:(NSArray<NSString *> *)keys
                 completion:(RNCAsyncStorageResultCallback)block
{

}

- (void)setValues:(NSArray<NSString *> *)values
          forKeys:(NSArray<NSString *> *)keys
       completion:(RNCAsyncStorageResultCallback)block
{
    for (int i = 0; i < keys.count; i++) {
        [self saveValue:values[i] forKey:keys[i]];
    }
}

- (void)valuesForKeys:(NSArray<NSString *> *)keys
           completion:(RNCAsyncStorageResultCallback)block
{

}

- (BOOL)isPassthrough
{
    return true;
}

// MARK: Private methods
- (NSString *)getMd5ForKey:(NSString *)key
{
    const char * pointer = [key UTF8String];
    unsigned char md5Buffer[CC_MD5_DIGEST_LENGTH];

    CC_MD5(pointer, (CC_LONG)strlen(pointer), md5Buffer);

    NSMutableString *string = [NSMutableString stringWithCapacity:CC_MD5_DIGEST_LENGTH * 2];
    for (int i = 0; i < CC_MD5_DIGEST_LENGTH; i++)
        [string appendFormat:@"%02x",md5Buffer[i]];

    return string;
}

- (void)saveValue:(NSString *)value
           forKey:(NSString *)key
{
    NSString *md5Key = [self getMd5ForKey:key];
    NSDictionary *infoPlistDict = [[NSBundle mainBundle] infoDictionary];
    NSString *appGroupId = infoPlistDict[@"AppGroupID"];
    NSURL *groupURL = [[NSFileManager defaultManager]
                       containerURLForSecurityApplicationGroupIdentifier:appGroupId];
    NSURL *fileURL = [groupURL URLByAppendingPathComponent:
                      [NSString stringWithFormat:@"Library/Caches/%@", md5Key]];
    NSError *error;
    [value writeToURL:fileURL
           atomically:YES
             encoding:NSUTF8StringEncoding
                error:&error];
    RCTLog(@"SAVE_VALUE_ERROR: %@", error);
}

@end
