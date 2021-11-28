# VividView
A ViewGroup that continually animates a stack of two or more children using scaling and fading effects.

![GIF Example](https://github.com/nihk/VividView/blob/master/example.gif)

Simply use the `VividView` class either programmatically or in your XML like so:

```
<ca.nihk.vividview.VividView
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:duration="3000"
    app:scale_start="1.0"
    app:scale_end="1.25"
    app:fade_fraction_start="0.8"
    app:start_immediately="false">

    <!-- Make sure to have at least two children here before VividView.start() is called -->

</ca.nihk.vividview.VividView>
```

The custom attributes are optional and default to the values you see above.  
* `duration` defines how long the combined scaling/fading animation lasts.  
* `scale_start` and `scale_end` is what your `View`'s scale-x and scale-y properties should start and end at, respectively.  
* `fade_fraction_start` is by what percent of the `duration` the fade should start, e.g. for a `duration` of 10000 and a  `fade_fraction_start` of 0.9, the fading part of the animation would start after nine seconds of scaling.    
* `start_immediately` defines whether the animation should start right away or not. If this is set to false then programmatically you can call `VividView.start()` to start the animation.  
* You can also define the interpolator programmatically. By default it's a `LinearInterpolator`.  

Call `VividView.start()` or `VividView.stop()` as you see fit to control the state of the animation.
