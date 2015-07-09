SmartPantry
==========
SmartPantry helps you keep track of your groceries and your shopping list. It is available for free from the [Play store](https://play.google.com/store/apps/details?id=macadamian.smartpantry).

- Scan barcodes using the camera for quick entry of items
- Manage different locations for your items
- Manage your items expiry dates and quantity remaining
- Get notified when an item is about to expire

[Macadamian](http://www.macadamian.com) is excited to release SmartPantry as an open-source project under the Apache 2.0 license, and we strongly encourage contributions to the repository. For more information on how you can contribute, please see the [contributing](#contributing) section below.

### Table of Contents
- [Installation](#installation)
- [Project Structure](#project-structure)
- [Testing](#testing)
- [Contributing](#contributing)
- [Acknowledgements](#acknowledgements)

Installation
=========
### Requirements
- [Android Studio](https://developer.android.com/sdk/index.html) to build the project
- Device with Android 5.0 or higher
- **Note**: the app has only been tested on a Nexus 5.

### Installation Instructions
1. Clone the repo: `git clone https://github.com/Macadamian/smart-pantry.git`
2. In Android Studio select "Open existing project" and select the `SmartPantry` folder.
3. Plug in your device and click the run button to build the app

Project Structure
=========
The folder structure for the source files is as follows:
```
|--content
    |--action
    |--controllers
|--database
    |--readers
    |--repositories
    |--tables
|--ui
    |--activities
    |--adapters
    |--fragments
    |--receivers
|--utility
    |--analytics
    |--comparators
|--widgets

```

All strings are stored in `res/values/strings.xml`.

Testing
=========
There are several testing classes (in progress) included in the project that cover some basic unit tests. To run them, right click on the `macadamian.smartpantry.tests` package in Android Studio and select "Run All Tests".

Contributing
=========
We welcome any contributions to the repository, and will make an effort to review Pull Request's frequently.

All bugs are tracked using GitHub's issue tracker, and we request that all patches should address an issue from there. 

Acknowledgements
=========
The following open source libraries were used in the project. Thank you!

- [Android Swipe-to-Dismiss](https://github.com/romannurik/Android-SwipeToDismiss)
- [Cursor Recycler Adapter](https://gist.github.com/Shywim/127f207e7248fe48400b)
- [ZXing](https://github.com/zxing/zxing)
- [Android SQLiteAssetHelper](https://github.com/jgilfelt/android-sqlite-asset-helper)
- [Guava](https://github.com/google/guava)
- [Apache Commons Lang](https://github.com/apache/commons-lang)
- [Android Universal Image Loader](https://github.com/nostra13/Android-Universal-Image-Loader)
- [Floating Action Button](https://github.com/makovkastar/FloatingActionButton)
- [Material Edit Text](https://github.com/rengwuxian/MaterialEditText)
- [Slidr](https://github.com/r0adkll/Slidr)
- [ShowcaseView](https://github.com/amlcurran/ShowcaseView)
