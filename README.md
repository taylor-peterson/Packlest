# Packlest
Composable checkists!

# Backup/Restore
Things are complicated by a desire to be able to make edits to the data, which makes adb's backup/restore a poor option, by the permissions Android puts on the file (meaning adb pull nor push will work).

* Open up Android Studio
* Enable debugging on your device
* Use the terminal in the IDE to run the below

```
adb shell run-as com.example.packlest cat /data/data/com.example.packlest/files/packlest_data.json > packlest_data.json
```

* Edit the file to your heart's content.

```
adb push packlest_data.json /data/local/tmp/packlest_data.json
```

* Make sure that Packlest is closed before the following or the new file will be overwritten.

```
adb shell run-as com.example.packlest cp /data/local/tmp/packlest_data.json /data/data/com.example.packlest/files/packlest_data.json
```

This workflow also enables editing the data in the emulator with mouse/keyboard, which is a fair bit easier than on device.
