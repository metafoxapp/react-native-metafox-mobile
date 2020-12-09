
#import "RNPhpfoxImageCompresser.h"
#import "React/RCTConvert.h"
#import <UIKit/UIKit.h>
#import <Photos/Photos.h>
#import <React/RCTLog.h>

@implementation RNPhpfoxImageCompresser

RCT_EXPORT_MODULE()

- (CGFloat)compressionQuality: (UIImage *) image
                    sizeLimit: (NSUInteger) limit
{
    NSData *data = UIImageJPEGRepresentation(image, 1.0);
    NSUInteger size = [data length];
    
    if (size > limit && limit > 0) {
        CGFloat result = (1.0 / ((CGFloat) size / limit));
        return result;
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
        NSString * destination = nil;
        NSString *source = [RCTConvert NSString:options[@"path"]];
        NSString *mime = [RCTConvert NSString:options[@"mime"]];
        CGFloat limit = [RCTConvert CGFloat:options[@"limit"]];
        BOOL forceCompress = [RCTConvert BOOL:options[@"forceCompress"]];
        
        NSData *originalData = [self dataWithPhAssetUrl:source];
        UIImage *originImage = [UIImage imageWithData:originalData];
        NSUInteger originFileSize = [originalData length];
                
        NSData *imageData = nil;
        NSUInteger fileSize = originFileSize;
        BOOL isHeic = [mime isEqualToString:@"video/quicktime"] || [mime isEqualToString:@"image/heic"];
        NSString *compressMime = nil;
        NSString *imageName = nil;
        BOOL shouldCompress = isHeic || forceCompress;
        
        // Step 1: heic file, mov image file -> auto compress to jpeg
        if (shouldCompress) {
            imageData = UIImageJPEGRepresentation(originImage, 1.0);
            
            originImage = [UIImage imageWithData:imageData];
            fileSize = [imageData length];
        }
        
        CGFloat quality = [self compressionQuality:originImage sizeLimit:limit];
        
        // Step 2: compress image file -> max file length <= limit
        for (; quality > 0.1 && fileSize > limit && limit > 0; quality -= 0.05) {
            imageData = UIImageJPEGRepresentation(originImage, quality);
            fileSize = [imageData length];
        }
        
        BOOL compressSuccess = (imageData != nil && limit > 0 && fileSize <= limit) || shouldCompress;

        if (compressSuccess) {
            imageName = [NSString stringWithFormat:@"%@.jpeg", [[NSUUID UUID] UUIDString]];
            NSString *imagePath = [NSString pathWithComponents:@[NSTemporaryDirectory(), imageName]];
            
            [[NSFileManager defaultManager] createFileAtPath:imagePath
                                                    contents:imageData
                                                  attributes:nil];
           
            compressMime = @"image/jpeg";
            destination = [[NSURL fileURLWithPath: imagePath] absoluteString];
        } else {
            destination = source;
        }

        NSDictionary *result  = @{
                                  @"limit": @(limit),
                                  @"path": destination,
                                  @"filesize": @(fileSize),
                                  @"quality": @(quality),
                                  @"original_path": source,
                                  @"original_filesize": @(originFileSize),
                                  @"compress_success":@(compressSuccess),
                                  @"isHeic":@(isHeic),
                                  @"mime": compressMime ? compressMime : [NSNull null],
                                  @"filename": imageName ? imageName : [NSNull null]
                                  };

        successCallback(@[result]);
    } @catch (NSException *exception) {
        failureCallback([self exceptionToError: exception]);
    }
}
@end

