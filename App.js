import React, { Component } from 'react';
import {
  Button,
  Platform,
  StyleSheet,
  Text,
  View
} from 'react-native';
import {
  NativeEventEmitter,
  NativeModules
} from 'react-native';

const ChirpConnect = NativeModules.ChirpConnect;
const ChirpConnectEmitter = new NativeEventEmitter(ChirpConnect);

const key = '';
const secret = '';
const licence = '';

export default class App extends Component<{}> {

  constructor(props) {
    super(props);
    this.state = {
      'status': 'Sleeping',
      'data': '----------'
    }
  }

  componentDidMount() {

    this.onStateChanged = ChirpConnectEmitter.addListener(
      'onStateChanged',
      (body) => {
        if (body.status === ChirpConnect.CHIRP_CONNECT_STATE_STOPPED) {
          this.setState({ status: 'Stopped' });
        } else if (body.status === ChirpConnect.CHIRP_CONNECT_STATE_PAUSED) {
          this.setState({ status: 'Paused' });
        } else if (body.status === ChirpConnect.CHIRP_CONNECT_STATE_RUNNING) {
          this.setState({ status: 'Running' });
        } else if (body.status === ChirpConnect.CHIRP_CONNECT_STATE_SENDING) {
          this.setState({ status: 'Sending' });
        } else if (body.status === ChirpConnect.CHIRP_CONNECT_STATE_RECEIVING) {
          this.setState({ status: 'Receiving' });
        }
      }
    );
    this.onSending = ChirpConnectEmitter.addListener(
      'onSending',
      (body) => {
        if (body.data) {
          this.setState({ data: body.data });
        }
      }
    );
    this.onReceived = ChirpConnectEmitter.addListener(
      'onReceived',
      (body) => {
        if (body.data) {
          this.setState({ data: body.data });
        }
      }
    )
    const onError = ChirpConnectEmitter.addListener(
      'onError',
      (body) => {
        console.warn(body.message);
      }
    )

    ChirpConnect.init(key, secret);
    ChirpConnect.setLicence(licence);
    ChirpConnect.start();
  }

  componentWillUnmount() {
    this.onStateChanged.remove();
    this.onSending.remove();
    this.onReceived.remove();
    this.onError.remove();
  }

  onPress() {
    ChirpConnect.sendRandom();
  }

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          Welcome to Chirp Connect!
        </Text>
        <Text style={styles.instructions}>
          {this.state.status}
        </Text>
        <Text style={styles.instructions}>
          {this.state.data}
        </Text>
      <Button onPress={this.onPress} title='SEND' />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 60,
  },
  instructions: {
    padding: 10,
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});
