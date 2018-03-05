# Chirp Connect for React Native

See [Getting Started with React Native](https://facebook.github.io/react-native/docs/getting-started.html)

Under the Building Projects with Native Code tab.


## iOS

Open the xcode project in the `/ios` folder, and build first of all.
See Troubleshooting section.

Then follow Steps 1 - 3 at [Getting Started [iOS]](https://developers.chirp.io/connect/getting-started/ios/) to include the Chirp Connect SDK into your project.

Copy `RCTChirpConnect.m` and `RCTChirpConnect.h` to your project.


## Android

Open the `/android` folder in Android Studio, and check the project builds.
See Troubleshooting section.

Then follow Steps 1 - 2 at [Getting Started [Android]](https://developers.chirp.io/connect/getting-started/android/) to include the Chirp Connect SDK into your project.

Copy `RCTChirpConnectModule.java` and `RCTChirpConnectPackage.java` to the project.

Import into your MainApplication.java

```java
import com.chirpconnect.rctchirpconnect.RCTChirpConnectPackage;
```

Add the package to the `getPackages` function

```java
@Override
  protected List<ReactPackage> getPackages() {
    return Arrays.<ReactPackage>asList(
        new MainReactPackage(),
        new RCTChirpConnectPackage()
    );
  }
```


## Application

Now the setup is complete, you can add ChirpConnect to your React Native application. In App.js

```javascript
import { NativeEventEmitter, NativeModules } from 'react-native';
const ChirpConnect = NativeModules.ChirpConnect;
const ChirpConnectEmitter = new NativeEventEmitter(ChirpConnect);

export default class App extends Component<{}> {

  componentDidMount() {

    this.onReceived = ChirpConnectEmitter.addListener(
      'onReceived',
      (event) => {
        if (event.data) {
          this.setState({ data: event.data });
        }
      }
    )
    const onError = ChirpConnectEmitter.addListener(
      'onError', (event) => { console.warn(event.message) }
    )

    ChirpConnect.init(key, secret);
    ChirpConnect.start();

    ChirpConnect.sendRandom();
  }

  componentWillUnmount() {
    this.onReceived.remove();
    this.onError.remove();
  }
}
```


## TroubleShooting

React Native with native support doesn't work well out of the box, so here
are some things that can go wrong.

### iOS

- Add react-native modules to header search paths. Open ios/<project>.xcodeproj.
Go to Project -> Build Settings -> Search Paths -> Header Search Paths
add the following with recursive set.

    `$(SRCROOT)/../node_modules/react-native/React`

- Create js bundle [No main.jsbundle found]

    `react-native bundle --entry-file ./index.js --platform ios --bundle-output ios/main.jsbundle`

- Fix third party modules [config.h file not found]

    `cd node_modules/react-native/third-party/glog-0.3.4`

    `../../scripts/ios-configure-glog.sh`

### Android

- Use java 8
[Could not initialize class com.android.sdklib.repository.AndroidSdkHandler]

    `brew tap caskroom/versions`
    `brew cask install java8`
    `export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_162.jdk/Contents/Home`

- Upgrade gradle to 2.3.3 by updating `build.gradle`

