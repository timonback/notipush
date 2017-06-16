#!/bin/bash

FIREBASE_URI=https://your-project.firebaseio.com
DEVICE_ID="fc1******:*****"
API_KEY="AA*****"
DATABASE_KEY="ly*****"

MESSAGE="test"
TOPIC="general"
AUTHOR="Admin"
DATE=$(date +%s)

while getopts ":m:t::a" o; do
    case "${o}" in
        m)
            MESSAGE=${OPTARG}
            ;;
        t)
            TOPIC=${OPTARG}
            ;;
        a)
            AUTHOR=${OPTARG}
            ;;
        *)
            echo "Usage: $0 -t topic -m message [-a author]" 1>&2; exit 1;
            ;;
    esac
done

echo "Sending to channel <$TOPIC> from <$AUTHOR>: $MESSAGE"

# "condition": "'dogs' in topics || 'cats' in topics",

# you can send to direct devices with the device ID or use topics that clients have previously subscribed to.
TO="$DEVICE_ID"
TO="/topics/$TOPIC"
curl -X POST \
  --header "Authorization: key=$API_KEY" \
  --Header "Content-Type: application/json" \
  https://fcm.googleapis.com/fcm/send \
  -d "{\"to\":\"$TO\",\"notificationREMOVED\":{\"body\":\"Show this notification directly, no processing in the app\"},\"data\":{\"header\":\"Yellow\",\"category\":\"$TOPIC\", \"message\":\"$MESSAGE\"},\"priority\":10}"

# persist data also in the database
curl -X POST \
  "$FIREBASE_URI/notification/$TOPIC.json?auth=$DATABASE_KEY" \
  -d "{\"message\":\"$MESSAGE\",\"source\":\"$AUTHOR\",\"date\":\"$DATE\"}"
