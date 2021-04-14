//
//  RNPhpfoxCallState.m
//  RNPhpfoxMobile
//
//  Created by Nguyễn Thiện on 13/04/2021.
//

#import "RNPhpfoxCallState.h"
@import CallKit;

typedef void (^CallBack)();
@interface RNPhpfoxCallState()

@property(strong, nonatomic) RCTResponseSenderBlock block;
@property(strong, nonatomic) CXCallObserver* callObserver;

@end
@implementation RNPhpfoxCallState

- (NSArray<NSString *> *)supportedEvents {
    return @[@"PhoneCallStateUpdate"];
}

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(addCallBlock:(RCTResponseSenderBlock) block) {
    // Setup call tracking
    self.block = block;
    self.callObserver = [[CXCallObserver alloc] init];
    __typeof(self) weakSelf = self;
    [self.callObserver setDelegate:weakSelf queue:nil];
}

RCT_EXPORT_METHOD(startListener) {
    // Setup call tracking
    self.callObserver = [[CXCallObserver alloc] init];
    __typeof(self) weakSelf = self;
    [self.callObserver setDelegate:weakSelf queue:nil];
}

RCT_EXPORT_METHOD(stopListener) {
    // Setup call tracking
    self.callObserver = nil;
}

- (void)callObserver:(CXCallObserver *)callObserver callChanged:(CXCall *)call {
    if (call.hasEnded == true) {
      [self sendEventWithName:@"PhoneCallStateUpdate" body:@"Disconnected"];
    } else if (call.hasConnected == true) {
      [self sendEventWithName:@"PhoneCallStateUpdate" body:@"Connected"];
    } else if (call.isOutgoing == true) {
      [self sendEventWithName:@"PhoneCallStateUpdate" body:@"Dialing"];
    } else {
      [self sendEventWithName:@"PhoneCallStateUpdate" body:@"Incoming"];
    }
}

@end
