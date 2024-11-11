IMPORTANT NOTES:
- If you want to be able to use the map functionality, you need to create a variable in your 
secrets.properties file to hold the API key we have used. "MAPS_API_KEY=...". The map will not
work properly if you don't have a process API access key
- When using the simulator to test, the "current location" of the device will always be in 
California, not the current location of the device you are working on
- In order to properly use this app and all it's features, you need to grant access for your location
to be used all the time. For newer versions of Android, you have to prompt the user to go into settings
and change to "Always Allow", you CANNOT ask for this permission directly in the app, only for "Once"
or "While Using App". Due to this, you might need to refresh the app after granting those permissions