//
//  RCTChirpConnect.m
//  ChirpConnect
//
//  Created by Joe Todd on 19/02/2018.
//  Copyright © 2018 Asio Ltd. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RCTChirpConnect.h"

@implementation RCTChirpConnect

ChirpConnect *sdk;

RCT_EXPORT_MODULE();

- (NSDictionary *)constantsToExport
{
  return @{
    @"CHIRP_CONNECT_STATE_STOPPED": [NSNumber numberWithInt:CHIRP_CONNECT_STATE_STOPPED],
    @"CHIRP_CONNECT_STATE_PAUSED": [NSNumber numberWithInt:CHIRP_CONNECT_STATE_PAUSED],
    @"CHIRP_CONNECT_STATE_RUNNING": [NSNumber numberWithInt:CHIRP_CONNECT_STATE_RUNNING],
    @"CHIRP_CONNECT_STATE_SENDING": [NSNumber numberWithInt:CHIRP_CONNECT_STATE_SENDING],
    @"CHIRP_CONNECT_STATE_RECEIVING": [NSNumber numberWithInt:CHIRP_CONNECT_STATE_RECEIVING]
  };
}

- (NSArray<NSString *> *)supportedEvents
{
  return @[
     @"onStateChanged",
     @"onSending",
     @"onSent",
     @"onReceiving",
     @"onReceived",
     @"onError"
  ];
}

/*
 * init()
 *
 * Initialise the SDK with an application key and secret.
 * Callbacks are also set up here.
 */
RCT_EXPORT_METHOD(init:(NSString *)key secret:(NSString *)secret)
{
  sdk = [[ChirpConnect alloc] initWithAppKey:key
                                   andSecret:secret];

  [sdk setStateUpdatedBlock:^(CHIRP_CONNECT_STATE oldState,
                              CHIRP_CONNECT_STATE newState)
  {
    [self sendEventWithName:@"onStateChanged" body:@{@"status": [NSNumber numberWithInt:newState]}];
  }];
  [sdk setSendingBlock:^(NSData * _Nonnull data)
  {
    NSString *payload = [[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding];
    [self sendEventWithName:@"onSending" body:@{@"data": payload}];
  }];
  [sdk setSentBlock:^(NSData * _Nonnull data)
  {
    NSString *payload = [[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding];
    [self sendEventWithName:@"onSent" body:@{@"data": payload}];
  }];
  [sdk setReceivingBlock:^(void)
  {
    [self sendEventWithName:@"onReceiving" body:@{}];
  }];
  [sdk setReceivedBlock:^(NSData * _Nonnull data)
  {
    NSString *payload = [[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding];
    [self sendEventWithName:@"onReceived" body:@{@"data": payload}];
  }];
}

/*
 * setLicence()
 *
 * Configure the SDK with a licence string.
 */
RCT_EXPORT_METHOD(setLicence:(NSString *)licence)
{
  NSError *err = [sdk setLicenceString:licence];
  if (err) {
    [self sendEventWithName:@"onError" body:@{@"message": [err localizedDescription]}];
  }
}

/*
 * start()
 *
 * Starts the SDK.
 */
RCT_EXPORT_METHOD(start)
{
  NSError *err = [sdk start];
  if (err) {
    [self sendEventWithName:@"onError" body:@{@"message": [err localizedDescription]}];
  }
}

/*
 * stop()
 *
 * Stops the SDK.
 */
RCT_EXPORT_METHOD(stop)
{
  NSError *err = [sdk stop];
  if (err) {
    [self sendEventWithName:@"onError" body:@{@"message": [err localizedDescription]}];
  }
}

/*
 * send()
 *
 * Sends a payload of NSData to the speaker.
 */
RCT_EXPORT_METHOD(send: (NSData *)data)
{
  NSError *err = [sdk send:data];
  if (err) {
    [self sendEventWithName:@"onError" body:@{@"message": [err localizedDescription]}];
  }
}

/*
 * sendRandom()
 *
 * Sends a random payload to the speaker.
 */
RCT_EXPORT_METHOD(sendRandom)
{
  NSData *data = [sdk randomPayloadWithLength:[sdk maxPayloadLength]];
  NSError *err = [sdk send:data];
  if (err) {
    [self sendEventWithName:@"onError" body:@{@"message": [err localizedDescription]}];
  }
}

@end
