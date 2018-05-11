/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  TouchableOpacity,
	NativeModules,
	DeviceEventEmitter
} from 'react-native';

var RNAndroid = NativeModules.RNModule;

type Props = {};
export default class App extends Component<Props> {

	componentWillMount() {
		DeviceEventEmitter.addListener('EventName', (msg) => {
			console.log(msg);
			alert("msg = "+msg.data)
		});
	}

  render() {
    return (
      <View style={styles.container}>
        <TouchableOpacity onPress={this.showToast.bind(this)}>
        <Text style={styles.welcome}>
          Show HelloWorld Toast
        </Text>
        </TouchableOpacity>

	      <TouchableOpacity onPress={this.sendDat2Rn.bind(this)}>
		      <Text style={styles.welcome}>
			      adnroid send data to RN
		      </Text>
	      </TouchableOpacity>
	      <TouchableOpacity onPress={this.startActivity.bind(this)}>
		      <Text style={styles.welcome}>
			      Go android Activity
		      </Text>
	      </TouchableOpacity>
      </View>
    );
  }

  showToast(){
    RNAndroid.showMyToast("Hello world!",()=>{})
  }

  sendDat2Rn(){
	  RNAndroid.sendDat2RN()
  }

	startActivity(){
		RNAndroid.startActivity("com.rnandroid.HelloWorldActivity","我是传递参数，你好呀!")
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
    margin: 10,
  },
});
