![alt text][logo]

### A better way to write with FHIR in Android.
---

## About

Ordinary Android TextViews allow users to set a static string, and require manually changing the value via the ```setText(String s)``` at runtime, if needed. The goal with the [CharcoalTextView](charcoal/src/main/java/charcoal/ehealthinnovation/org/charcoaltextview/view/CharcoalTextView.java) is to allow a developer to ```setObservation(Observation obs)```, instead of setting a static String value, and have the field update automatically, based on preset configurations for that field, or any preference changes the user may make.

---

## How to Use

#### Initializing your UCUM units for the project.

1. Get, or create the UCUM essence.xml you will be using for this project. If you are new to UCUM, click [here](UCUM.md). Our example file is located [here](example/src/main/assets/essence.xml). Once you have the file, place it in your project's asset folder.

2. Add the annotation ```@Essence(asset = YOUR_ESSENCE_FILE_NAME.xml)``` to the main application or activity class for your project.

3. Call ```Charcoal.bind(this);```

Your app should now have a valid instance of a UcumService, which you use to parse and convert any FHIR type Observation to and from UCUM standard units.

This UcumService is a singleton object that can be accessed at any point using the [EssenceController](charcoal/src/main/java/charcoal/ehealthinnovation/org/charcoaltextview/controller/EssenceController.java). For more information on how to work with, or convert units manually using this service, please checkout the [additional information on UCUM](UCUM.md).

#### Using the CharcoalTextView

1. Change the Android ```TextView``` in your layout.xml file, to instead be a ```CharcoalTextView```.
2. In your java Activity/Fragment/Dialog/etc... class, add the following annotation above the field:

```@Charcoal(property = YOUR_PROPERTY, defaultUnit = YOUR_UNIT, accuracy = YOUR_ACCURACY, format = YOUR_FORMAT)```

  * ```YOUR_PROPERTY``` is a String that indicates the medical property that this measurement represents, ie 'blood_glucose', 'weight', etc. These codes are internal to your own application, and have no relation to UCUM fields or unit properties. They are used to associate unit changes and updates to a subset of all the CharcoalTextViews in your app. So if you want to only change the mass unit for weight measurements in your application, you would give only those weight views the same property ```String YOUR_PROPERTY = "weight_reading"```.
     
  * ```YOUR_UNIT``` is the String that unit that will be used with this field. For conversion and display to work properly, it needs to adhere to UCUM standard unit notation. For example, if we wanted to use the unit ```mmHg``` to display blood pressure readings, we would need to use both the notation for ```meter of mercury column```combined with the notation for the prefix ```milli```:

```xml     
    <unit xmlns="" Code="m[Hg]" CODE="M[HG]" isMetric="yes" class="clinical">
        <name>meter of mercury column</name>
        <printSymbol>m&#160;Hg</printSymbol>
        <property>pressure</property>
        <value Unit="kPa" UNIT="KPAL" value="133.3220">133.3220</value>
    </unit>
```

```xml
    <prefix xmlns="" Code="m" CODE="M">
        <name>milli</name>
        <printSymbol>m</printSymbol>
        <value value="1e-3">1 &#215; 10<sup>-3</sup>
        </value>
    </prefix>
```
So, our field would be set as follows, ```String YOUR_UNIT = "mm[Hg]```.

  * ```YOUR_ACCURACY``` (optional field) is an integer value representing the number of digits to the right of the decimal point we want displayed for this field. Defaults to 2.
  
  * ```YOUR_FORMAT``` (optional field) is the String format for combined observation value and unit together. Defaults to "%1$s %2$s".

#### Changing Preferences

1. When you want to change the default unit for a given property, simply call ```PreferenceController.setUnitForProperty(Context ctx, String property, String unit)```. This will change all CharcoalTextViews assigned the given property to convert and display Observations with the new unit.

---

### TODOs! (in no particular order)
1. Stand alone unit and value fields if user wants to separate the text fields to display independantly.
2. Remove the need to call Charcoal.bind(Context ctx) to set the ```essence.xml``` file. This could probably be done with a preprocessor to set the filename as a variable or something along those lines.
3. Maven upload.
4. Add versioning tools to build.gradle file.
5. ~~Fix some of the folder structure and naming to be more intuitive (ie, why is preferences a directory that holds controllers...)~~
6. Improve the example application.
7. Actually write out the UCUM.md readme to be helpful instead of just copy pasting the pharmacy guide.
8. ~~Dynamic accuracy for different units.~~
9. remove .idea/ stuff...

License
-------

    Copyright 2017 Mark Iantorno

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

Notes
-------
If you like the font, it's the [Charcoal Font](http://www.fontspace.com/jonathan-s-harris/charcoal) by Jonathan S. Harris. 

Special thanks to James Agnew for guidance with FHIR and UCUM standards.     
     
[logo]: https://github.com/markiantorno/Charcoal/blob/staging/CHARCOAL.png
