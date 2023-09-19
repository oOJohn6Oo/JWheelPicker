#### [JWheelPicker]

* iOS style wheel picker
* For single wheel picker, use [JSinglePickerDialogFragment], need to pass List<T>
* For multiple wheel picker, use [JMultiplePickerDialogFragment], need to implement your own [IMultipleJPickerAdapter]

#### Implementation

- add repo `maven { url 'https://jitpack.io' }`
- latest version ![LatestVersion]
- add `implementation("com.github.oOJohn6Oo:JWheelPicker:<version>")`


[LatestVersion]: https://jitpack.io/v/oOJohn6Oo/JWheelPicker.svg
[JWheelPicker]: ./JWheelPicker/src/main/kotlin/io/john6/johnbase/compose/picker/JWheelPicker.kt
[JSinglePickerDialogFragment]: ./JWheelPicker/src/main/kotlin/io/john6/johnbase/compose/picker/dialog/single/JSinglePickerDialogFragment.kt
[JMultiplePickerDialogFragment]: ./JWheelPicker/src/main/kotlin/io/john6/johnbase/compose/picker/dialog/multiple/JMultiplePickerDialogFragment.kt
[IMultipleJPickerAdapter]: ./JWheelPicker/src/main/kotlin/io/john6/johnbase/compose/picker/dialog/multiple/IMultipleJPickerAdapter.kt