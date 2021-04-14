//
//  RNPhpfoxCallState.h
//  Pods
//
//  Created by Nguyễn Thiện on 13/04/2021.
//

#if __has_include("RCTBridgeModule.h")
#import "RCTBridgeModule.h"
#import "RCTEventEmitter.h"
#else
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#endif
#import <Foundation/Foundation.h>
#import <CallKit/CallKit.h>

@interface RNPhpfoxCallState : RCTEventEmitter <RCTBridgeModule, CXCallObserverDelegate>
@end
