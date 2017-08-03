# Notipush ![Notipush](https://github.com/timonback/notipush/blob/master/app/src/main/res/mipmap-hdpi/ic_launcher.png?raw=true)

Notipush is an android application that allows to receive instant push notifications from any source (via the Firebase Cloudmessaging API) and display also all past sent notifications.

## Setup

1. Set up the Firebase project
    1. Create a new project
    2. Set the *rules* of the Firebase database to:
    ```
    {
      "rules": {
        ".read": "auth != null",
        ".write": "false"
      }
    }
    ```
2. Install the app
    1. If required, change the *app/google-services.json* file to your project (download from the Firebase Settings). In essence, the Notifications and the Database needs to be enabled.
    2. Install the application on your phone
3. Send new notifications
    1. Checkout the *fcm.sh* file in the root directory of the project
    2. Add the required tokens and keys
    3. Run the script
    4. Check your phone and see the notification

## Docs
All updates for the docs have to added to the update/docs branch.

## Disclaimer
All notification that are sent are publicly accessable with the used Firebase *rules*. Be careful about what you share.
