#### [JWheelPicker]

<p float="left">
  <img src="https://raw.githubusercontent.com/github/explore/8baf984947f4d9c32006bd03fa4c51ff91aadf8d/topics/android/android.png"  width="24" />
  <img src="https://raw.githubusercontent.com/github/explore/4479d2a2c854198cb00160f8593519c14dc3b905/topics/kotlin/kotlin.png" width="24" />
  <img src="https://raw.githubusercontent.com/github/explore/ae48d1ca3274c0c3a90f872e605eaef069a16771/topics/jetpack-compose/jetpack-compose.png" width="24" />
</p>

* iOS-style wheel picker supports all Android versions starting from Android lollipop(5.0).
* For single wheel picker, use [JSinglePickerDialogFragment], a List<JWheelPickerItemInfo> is needed.
* For multiple wheels picker, use [JMultiplePickerDialogFragment], need to implement your own [IMultipleJPickerAdapter].
* Or you can create your own picker, using the [JWheelPicker], which is just a normal Compose UI.

|||
| ---- | ---- |
| <img src="https://github.com/oOJohn6Oo/JWheelPicker/assets/24718357/4e25e324-bfd5-4510-a522-a5f642581d3e" alt="JSinglePickerDialogFragment"/>|<img src="https://github.com/oOJohn6Oo/JWheelPicker/assets/24718357/360d5bc8-333a-4d5b-ba59-5d772bbda831" alt="JMultiplePickerDialogFragment"/>|

#### Implementation

- add repo `maven { url 'https://jitpack.io' }`
- latest version ![LatestVersion]
- add `implementation("com.github.oOJohn6Oo:JWheelPicker:<version>")`


[LatestVersion]: https://jitpack.io/v/oOJohn6Oo/JWheelPicker.svg
[JWheelPicker]: ./JWheelPicker/src/main/kotlin/io/john6/johnbase/compose/picker/JWheelPicker.kt
[JSinglePickerDialogFragment]: ./JWheelPicker/src/main/kotlin/io/john6/johnbase/compose/picker/dialog/single/JSinglePickerDialogFragment.kt
[JMultiplePickerDialogFragment]: ./JWheelPicker/src/main/kotlin/io/john6/johnbase/compose/picker/dialog/multiple/JMultiplePickerDialogFragment.kt
[IMultipleJPickerAdapter]: ./JWheelPicker/src/main/kotlin/io/john6/johnbase/compose/picker/dialog/multiple/IMultipleJPickerAdapter.kt
