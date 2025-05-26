### [JWheelPicker]

<p float="left">
  <img src="https://raw.githubusercontent.com/github/explore/8baf984947f4d9c32006bd03fa4c51ff91aadf8d/topics/android/android.png"  width="24" />
  <img src="https://raw.githubusercontent.com/github/explore/4479d2a2c854198cb00160f8593519c14dc3b905/topics/kotlin/kotlin.png" width="24" />
  <img src="https://raw.githubusercontent.com/github/explore/ae48d1ca3274c0c3a90f872e605eaef069a16771/topics/jetpack-compose/jetpack-compose.png" width="24" />
</p>

Demo APK could be download in [The latest release]

* iOS-style wheel picker supports all Android versions starting from Android lollipop(5.0).
* For single wheel picker, use [JSinglePickerDialogFragment], a [JSinglePickerDialogData] or [JSinglePickerDialogAdapterData] is needed.
* For multiple wheels picker, use [JMultiplePickerDialogFragment], a [JMultiPickerDialogData] is needed.
* For date picker, use [JDateWheelPickerDialogFragment], a [JDatePickerDialogData] is needed.
* Or you can create your own picker by using the [JWheelPicker], [JMultiWheelPicker] and [JDateWheelPicker] which are just normal Compose UI.
* WindowInsets are well handle by default inside these PickerDialogFragment

|||
| ---- | ---- |
| <img src="https://github.com/oOJohn6Oo/JWheelPicker/assets/24718357/4e25e324-bfd5-4510-a522-a5f642581d3e" alt="JSinglePickerDialogFragment"/>|<img src="https://github.com/oOJohn6Oo/JWheelPicker/assets/24718357/360d5bc8-333a-4d5b-ba59-5d772bbda831" alt="JMultiplePickerDialogFragment"/>|

#### Implementation
> JitPack no longer available
- latest version ![LatestVersion]
- add `implementation("com.github.ooJohn6oo:jwheelpicker:<version>")`

#### Usage

##### For Any Compose View Use

https://github.com/oOJohn6Oo/JWheelPicker/blob/edd59cea0ea8a9d1635c696a836c5eebe32e9793/JWheelPicker/src/main/kotlin/io/john6/base/compose/picker/dialog/single/JSinglePickerDialogFragment.kt#L141-L152

https://github.com/oOJohn6Oo/JWheelPicker/blob/edd59cea0ea8a9d1635c696a836c5eebe32e9793/JWheelPicker/src/main/kotlin/io/john6/base/compose/picker/dialog/multiple/JMultiplePickerDialogFragment.kt#L147-L155

https://github.com/oOJohn6Oo/JWheelPicker/blob/edd59cea0ea8a9d1635c696a836c5eebe32e9793/JWheelPicker/src/main/kotlin/io/john6/base/compose/picker/dialog/date/JDateWheelPickerDialogFragment.kt#L121-L132

##### For Default BottomSheet Dialog Use

https://github.com/oOJohn6Oo/JWheelPicker/blob/edd59cea0ea8a9d1635c696a836c5eebe32e9793/app/src/main/kotlin/io/john6/demo/wheelpicker/JWheelPickerDemoActivity.kt#L75-L104

https://github.com/oOJohn6Oo/JWheelPicker/blob/edd59cea0ea8a9d1635c696a836c5eebe32e9793/app/src/main/kotlin/io/john6/demo/wheelpicker/JWheelPickerDemoActivity.kt#L106-L116

https://github.com/oOJohn6Oo/JWheelPicker/blob/edd59cea0ea8a9d1635c696a836c5eebe32e9793/app/src/main/kotlin/io/john6/demo/wheelpicker/JWheelPickerDemoActivity.kt#L144-L162

And using FragmentResult API to get the callback

https://github.com/oOJohn6Oo/JWheelPicker/blob/91aae0b0f9344d1e870e66368ad6771e699d58d3/app/src/main/kotlin/io/john6/demo/wheelpicker/JWheelPickerDemoActivity.kt#L77-L84


#### Common Q&A

##### How can I customize the header of Picker Dialog
Just override the `ColumnScope.DefaultPickerHeader` function, like [CustomTitleSinglePickerDialogFragment].

#### How to disable the swipe-to-dismiss feature of the preset Picker Dialog?
When passing data like [JDatePickerDialogData], set the `isDraggable` to false.



[LatestVersion]: https://badgen.net/maven/v/maven-central/io.github.oojohn6oo/jwheelpicker
[JWheelPicker]: ./JWheelPicker/src/main/kotlin/io/john6/base/compose/picker/JWheelPicker.kt
[JMultiWheelPicker]: ./JWheelPicker/src/main/kotlin/io/john6/base/compose/picker/JMultiWheelPicker.kt
[JDateWheelPicker]: ./JWheelPicker/src/main/kotlin/io/john6/base/compose/picker/JDateWheelPicker.kt
[JSinglePickerDialogFragment]: ./JWheelPicker/src/main/kotlin/io/john6/base/compose/picker/dialog/single/JSinglePickerDialogFragment.kt
[JSinglePickerDialogData]: ./JWheelPicker/src/main/kotlin/io/john6/base/compose/picker/dialog/single/JSinglePickerDialogData.kt
[JSinglePickerDialogAdapterData]: ./JWheelPicker/src/main/kotlin/io/john6/base/compose/picker/dialog/single/JSinglePickerDialogAdapterData.kt
[JMultiplePickerDialogFragment]: ./JWheelPicker/src/main/kotlin/io/john6/base/compose/picker/dialog/multiple/JMultiplePickerDialogFragment.kt
[JMultiPickerDialogData]: ./JWheelPicker/src/main/kotlin/io/john6/base/compose/picker/dialog/multiple/JMultiPickerDialogData.kt
[JDateWheelPickerDialogFragment]: ./JWheelPicker/src/main/kotlin/io/john6/base/compose/picker/dialog/multiple/JDateWheelPickerDialogFragment.kt
[JDatePickerDialogData]: ./JWheelPicker/src/main/kotlin/io/john6/base/compose/picker/dialog/multiple/JDatePickerDialogData.kt
[IJPickerAdapter]: ./JWheelPicker/src/main/kotlin/io/john6/base/compose/picker/dialog/IJPickerAdapter.kt
[CustomTitleSinglePickerDialogFragment]: ./app/src/main/kotlin/io/john6/demo/wheelpicker/CustomTitleSinglePickerDialogFragment.kt
[The latest release]: https://github.com/oOJohn6Oo/JWheelPicker/releases/latest