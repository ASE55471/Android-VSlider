## VSlider-Android
An rewritten seekerbar supports both vertical and horizontal with some useful features. 

### max:integer
Max value

### orientation:vertical | horizontal
Orientation

### progressDrawable:color | reference
Background drawable. In drawable layer-list first layer is background, second layer is upper progress drawble, third layer is lower drawbler. If set progressDrawableDisplayType to start or end, only first two layer been used, If set to middle the all three layers been used.

### progress:integer
Initial value, must lower then max value 

### progressStart:integer
Background progress bar starting position, must lower then max value 

### progressDrawableDisplayType:start | middle | end
Backgorund drawable display type

### thumbnail:reference
Thumbnail drawable

### thumbnailPress:reference
Thumbnail drawable when pressed

### progressDrawableMinWidth:dimension
Background drawable minimum width

### touchAreaRatio:float
Touch area ratio base on thumbnail size. If this value is 1.2 the touch area is thumbnail 1.2 times bigger

### thumbnailDrawableRatio:float
Thumbnail scale ratio

### thumbnailPressDrawableRatio:float
Thumbnail scale ratio when pressed

### relativeTouchPoint:boolean
If this mode is on the thumbnail center won't jump to your finger position when pressed.

### rotateThumbnailDrawableDegree:integer
Thumbnail angle

### progressAreaOffset:dimension
Progress area offset

### step:integer
Span of each step, Must lower then max value. If max is 49 and step is 5, the progress is 10 step and last span of step is 4 , but if you call setProgress in realtime is still free between max.
