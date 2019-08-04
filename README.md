[ ![Download](https://api.bintray.com/packages/nickjrose/scalerfader/ca.nihk.scalerfader/images/download.svg?version=0.0.1) ](https://bintray.com/nickjrose/scalerfader/ca.nihk.scalerfader/0.0.1/link)

# ScalerFader
A ViewGroup that continually animates a stack of two or more children using scaling and fading effects.

![GIF Example](https://github.com/nihk/ScalerFader/blob/master/example.gif)

You can add it as a gradle dependency like so:

Add this to your project-level build.gradle's `allprojects.repositories` block.
```
maven { url "https://dl.bintray.com/nickjrose/scalerfader/" }
```

Then add this to your app-level build.gradle's `dependencies` block.
```
implementation "ca.nihk:scalerfader:0.0.1"
```

Then simply use the `ScalerFader` class either programmatically or in your XML like so:

```
<ca.nihk.scalerfader.ScalerFader
    android:id="@+id/scaler_fader"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:duration="3000"
    app:fade_fraction_start="0.8"
    app:scale_end="1.25"
    app:scale_start="1.0"
    app:start_immediately="false">

    <!-- Make sure to have at least two children here before ScalerFader.start() is called -->

</ca.nihk.scalerfader.ScalerFader>
```

The custom attributes are optional and default to the values you see above.

Call `ScalerFader.start()` or `ScalerFader.reset()` as you see fit to control the state of the animation.
