
#import "RNPhpfoxImageCompresser.h"
#import "React/RCTConvert.h"
#import <UIKit/UIKit.h>
#import <Photos/Photos.h>

@implementation RNPhpfoxImageCompresser

RCT_EXPORT_MODULE()

- (CGFloat)compressionQuality:  (UIImage *) image
                    sizeLimit: (NSUInteger) limit
{
    NSData *data = UIImageJPEGRepresentation(image, 1.0);
    NSUInteger size = [data length];

    if(size > limit) {
        return 1.0 - limit / size;
    }
    return 1.0;
}

- (NSError *)exceptionToError: (NSException * )exception
{
    NSMutableDictionary * info = [NSMutableDictionary dictionary];

    [info setValue:exception.name
            forKey:@"ExceptionName"];
    [info setValue:exception.reason
            forKey:@"ExceptionReason"];
    [info setValue:exception.callStackReturnAddresses forKey:@"ExceptionCallStackReturnAddresses"];
    [info setValue:exception.callStackSymbols
            forKey:@"ExceptionCallStackSymbols"];
    [info setValue:exception.userInfo
            forKey:@"ExceptionUserInfo"];

    return [[NSError alloc] initWithDomain:@"error" code:-1 userInfo:info];
}

- (NSData * _Nullable) dataWithPhAssetUrl: (NSString *) url
{
    __block NSData *originalData =  nil;
    NSString * localIdentifier = [url stringByReplacingOccurrencesOfString:@"ph://" withString:@""];
    PHImageManager *manager  = [PHImageManager defaultManager];
    PHImageRequestOptions *option = [[PHImageRequestOptions alloc] init];
    option.synchronous = true;

    [[PHAsset fetchAssetsWithLocalIdentifiers:@[localIdentifier]
                                      options:nil]
     enumerateObjectsUsingBlock:^(PHAsset*  _Nonnull photoAsset, NSUInteger idx, BOOL * _Nonnull stop) {
         if ([localIdentifier isEqualToString: photoAsset.localIdentifier])
         {
             [manager requestImageDataForAsset:photoAsset
                                       options:option resultHandler:^(NSData * _Nullable imageData, NSString * _Nullable dataUTI, UIImageOrientation orientation, NSDictionary * _Nullable info) {
                                           originalData = imageData;
                                       }];
         }
         *stop = YES;
    }];

    return originalData;
}

RCT_EXPORT_METHOD(compressImage:(NSDictionary *)options
                  failureCallback:(RCTResponseErrorBlock)failureCallback
                  successCallback:(RCTResponseSenderBlock)successCallback)
{
    @try {


        NSString * destination = [NSString pathWithComponents:@[NSTemporaryDirectory(),[[NSUUID UUID] UUIDString]]];

        NSString *source = [RCTConvert NSString:options[@"path"]];
        CGFloat limit = [RCTConvert CGFloat:options[@"limit"]];
        NSData *originalData =  [self dataWithPhAssetUrl:source];
        UIImage *originImage =  [UIImage imageWithData:originalData];
        NSUInteger originFileSize = [originalData length];
        NSUInteger fileSize = originFileSize;
        CGFloat quality = 1.0;
        NSData *imageData  = nil;

        for (; quality > 0.1 && fileSize > limit; quality -= 0.05) {
            imageData  = UIImageJPEGRepresentation(originImage, quality);
            fileSize =  [imageData length];
        }

        if(imageData != nil){
            [[NSFileManager defaultManager] createFileAtPath:destination
                                                    contents:imageData
                                                  attributes:nil];
            destination = [[NSURL fileURLWithPath: destination] absoluteString];
        }else{
            destination =  source;
        }

        NSDictionary *result  = @{
                                  @"limit": @(limit),
                                  @"path": destination,
                                  @"filesize": @(fileSize),
                                  @"quality": @(quality),
                                  @"original_path": source,
                                  @"original_filesize": @(originFileSize),
                                  };

        successCallback(@[result]);
    } @catch (NSException *exception) {
        failureCallback([self exceptionToError: exception]);
    }
}
@end

