#!/bin/bash

FIREBASE_URI=https://your-project.firebaseio.com
DEVICE_ID="fc1******:*****"
API_KEY="AA*****"
DATABASE_KEY="ly*****"

MESSAGE="test"

# "condition": "'dogs' in topics || 'cats' in topics",

# you can send to direct devices with the device ID or use topics that clients have previously subscribed to.
TO="$DEVICE_ID"
TO="/topics/news"
curl -X POST \
  --header "Authorization: key=$API_KEY" \
  --Header "Content-Type: application/json" \
  https://fcm.googleapis.com/fcm/send \
  -d "{\"to\":\"$TO\",\"notificationREMOVED\":{\"body\":\"Show this notification directly, no processing in the app\"},\"data\":{\"header\":\"Yellow\",\"category\":\"test\", \"message\":\"my message\"},\"priority\":10}"

# persist data also in the database
curl -X POST \
  $FIREBASE_URI/notification/test.json?auth=$DATABASE_KEY \
  -d "{\"message\":\"Testing123456798\",\"source\":\"Admin\",\"date\":\"195489641\"}"
