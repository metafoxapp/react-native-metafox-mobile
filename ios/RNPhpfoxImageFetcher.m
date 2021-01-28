//
//  RNPhpfoxImageFetcher.m
//  RNPhpfoxMobile
//
//  Created by Nguyễn Thiện on 27/01/2021.
// Original source at: https://gist.github.com/simonepauro/19404909462fe37304d9581cee00bfe2


#import "RNPhpfoxImageFetcher.h"
#import <Photos/Photos.h>
#import <React/RCTUtils.h>
#import <React/RCTImageLoader.h>

@implementation RNPhpfoxImageFetcher

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(saveToDocumentsFolder:(NSURL *)imageURL
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
  CGSize size = CGSizeZero;
  CGFloat scale = 1;
  RCTResizeMode resizeMode = RCTResizeModeContain;
  NSString *assetID = @"";
  PHFetchResult *results;

  if (!imageURL) {
    NSString *errorText = @"Cannot load a photo library asset with no URL";
    reject(0, errorText, NULL);
    return;
  } else if ([imageURL.scheme caseInsensitiveCompare:@"assets-library"] == NSOrderedSame) {
    assetID = [imageURL absoluteString];
    results = [PHAsset fetchAssetsWithALAssetURLs:@[imageURL] options:nil];
  } else {
    assetID = [imageURL.absoluteString substringFromIndex:@"ph://".length];
    results = [PHAsset fetchAssetsWithLocalIdentifiers:@[assetID] options:nil];
  }

  if (results.count == 0) {
    NSString *errorText = [NSString stringWithFormat:@"Failed to fetch PHAsset with local identifier %@ with no error message.", assetID];
    reject(0, errorText, NULL);
    return;
  }

  PHAsset *asset = [results firstObject];
  PHImageRequestOptions *imageOptions = [PHImageRequestOptions new];
  imageOptions.networkAccessAllowed = YES;
  imageOptions.deliveryMode = PHImageRequestOptionsDeliveryModeHighQualityFormat;

  BOOL useMaximumSize = CGSizeEqualToSize(size, CGSizeZero);
  CGSize targetSize;

  if (useMaximumSize) {
    targetSize = PHImageManagerMaximumSize;
    imageOptions.resizeMode = PHImageRequestOptionsResizeModeNone;
  } else {
    targetSize = CGSizeApplyAffineTransform(size, CGAffineTransformMakeScale(scale, scale));
    imageOptions.resizeMode = PHImageRequestOptionsResizeModeFast;
  }

  PHImageContentMode contentMode = PHImageContentModeAspectFill;
  if (resizeMode == RCTResizeModeContain) {
    contentMode = PHImageContentModeAspectFit;
  }

  [[PHImageManager defaultManager] requestImageForAsset:asset
                                             targetSize:targetSize
                                            contentMode:contentMode
                                                options:imageOptions
                                          resultHandler:^(UIImage *result, NSDictionary<NSString *, id> *info) {
    if (result) {
      UIImage *image = result;
      NSString *imageName = [assetID stringByReplacingOccurrencesOfString:@"/" withString:@"_"];
      NSString *imagePath = [self saveImageToDocuments:image withName:imageName];
      resolve(imagePath);
    } else {
      reject(0, @"image not found", NULL);
    }
  }];
}

-(NSString*) saveImageToDocuments:(UIImage *)image withName:(NSString *)name {
  NSData *imageData = UIImageJPEGRepresentation(image, 1);
  NSString *documentsDirectoryPath = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)[0];
  NSString *fileName = [NSString stringWithFormat:@"%@.jpg", name];
  NSString *path = [documentsDirectoryPath stringByAppendingPathComponent:fileName];

  [[NSFileManager defaultManager] createFileAtPath:path contents:imageData attributes:NULL];
  NSLog(@"\n\n\n\nRNPhpfoxImageFetcher: image saved \nname=%@\npath=%@\n\n\n", name, path);

  return path;
}

@end
