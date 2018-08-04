
# react-native-datausagecheck

## Getting started

`$ npm install react-native-datausagecheck --save`

  <center>OR</center>

`$ yarn add react-native-datausagecheck`
### Mostly automatic installation

`$ react-native link react-native-datausagecheck`

### Manual installation


#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.datausagecheck.RNDatausagecheckPackage;` to the imports at the top of the file
  - Add `new RNDatausagecheckPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-datausagecheck'
  	project(':react-native-datausagecheck').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-datausagecheck/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-datausagecheck')
  	```


## Usage
```javascript
import RNDatausagecheck from 'react-native-datausagecheck';

// TODO: What to do with the module?
RNDatausagecheck.currentTodayDataUsage((msg) => {
  console.log(Number(msg))
});
```
