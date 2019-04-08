
# react-native-phpfox-mobile

## Getting started

`$ npm install react-native-phpfox-mobile --save`

### Mostly automatic installation

`$ react-native link react-native-phpfox-mobile`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-phpfox-mobile` and add `RNPhpfoxMobile.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNPhpfoxMobile.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNPhpfoxMobilePackage;` to the imports at the top of the file
  - Add `new RNPhpfoxMobilePackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-phpfox-mobile'
  	project(':react-native-phpfox-mobile').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-phpfox-mobile/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-phpfox-mobile')
  	```

#### Windows
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `RNPhpfoxMobile.sln` in `node_modules/react-native-phpfox-mobile/windows/RNPhpfoxMobile.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Phpfox.Mobile.RNPhpfoxMobile;` to the usings at the top of the file
  - Add `new RNPhpfoxMobilePackage()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import RNPhpfoxMobile from 'react-native-phpfox-mobile';

// TODO: What to do with the module?
RNPhpfoxMobile;
```
  