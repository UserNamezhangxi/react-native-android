## RN 与原生的关系 ##

###  PART1:  RN 拉 android 原生的方法 ###

1.	定义一个 `model` 继承 `ReactContextBaseJavaModule`
	
		public class RNModule extends ReactContextBaseJavaModule {
		
		    public RNModule (ReactApplicationContext reactContext){
		        super(reactContext);
		    }
		
		    @Override
		    public String getName() {
		        return "RNModule";
		    }
		
		    @ReactMethod
		    public void showMyToast(String msg, Callback callback){
		        Toast.makeText(getReactApplicationContext(),msg,Toast.LENGTH_LONG).show();
		        callback.invoke("show toast");
		    }
		
		    @ReactMethod
		    public void startActivity(String name, String params){
		        try{
		            Activity currentActivity = getCurrentActivity();
		            if(null!=currentActivity){
		                Class toActivity = Class.forName(name);
		                Intent intent = new Intent(currentActivity,toActivity);
		                intent.putExtra("params", params);
		                currentActivity.startActivity(intent);
		            }
		        }catch(Exception e){
		            throw new JSApplicationIllegalArgumentException(
		                    "不能打开Activity : "+e.getMessage());
		        }
		    }
		}

	这里我添加了显示Toast 方法和启动Activity的方法。
	### 请注意 ###
	 
	- 1. 自己定义的原生方法一定要 添加 `@ReactMethod`注解，否则 `JS` 不识别；
	- 2. `getName()`方法的方的返回值就是我们后面要调用的名称
	

2.	注册刚刚定义的 `model` 实现`ReactPackage`
		
		public class RegisterJsReactPackage implements ReactPackage {
	
		    @Override
		    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
		        List<NativeModule> modules = new ArrayList<>();
		        modules.add(new RNModule(reactContext));
		        return modules;
		    }
		
		    @Override
		    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
		        return Collections.emptyList();
		    }
		}
	### 请注意 ###
		 
	- 1. 在`createNativeModules`方法中加入`modules.add(new RNModule(reactContext));`表示将我们的 `model` 注册在`NativeModule`中了
	- 2. `AndroidStuido`自动生成的`createViewManagers`方法返回值为`null`,后面运行是会坑的，所以这里需要改成` Collections.emptyList()；`

3.	使用：
		
		import {
		    ...
			NativeModules,
		} from 'react-native';		
		var RNAndroid = NativeModules.RNModule;
	
		showToast(){
	    	RNAndroid.showMyToast("Hello world!",()=>{})
	  	}
		
		startActivity(){
			RNAndroid.startActivity("com.rnandroid.HelloWorldActivity","我是传递参数，你好呀!")
	    }
	### 请注意 ###
	- 1.这里引入的`RNModule`就是我们在第1步中注意的第2点


	
###  PART2:  android 拉 RN 界面 ###

> android 启动RN 界面，这里我只是用一个按钮启动了一下。

1.	只要让RN 刚刚启动不加载默认的MainActivity ，加载我们自己的Activity就可以。

    创建自己的`activirt`

		public class MyReactActivity extends AppCompatActivity {
	
		    @Override
		    protected void onCreate(@Nullable Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        setContentView(R.layout.activity_react);
		        Button btn = (Button) findViewById(R.id.btn_gotorn);
		        btn.setOnClickListener(new View.OnClickListener() {
		            @Override
		            public void onClick(View v) {
		                Intent intent = new Intent(MyReactActivity.this,MainActivity.class);
		                startActivity(intent);
		            }
		        });
		    }
		}

	布局文件中就放了一个button。
	
	### 注意： ###
	别忘记了在清单文件中 修改启动的主Activity为 MyReactActivity

	`android:name=".MyReactActivity"`
	
	同时将原有的MainActivity 注册一下：

    `<activity android:name="com.rnandroid.MainActivity" />`

	这样app启动就先启动我们定义的原生的界面，点击原生的界面，拉起RN 的界面。在Rn界面点击按钮启动调用原生的方法，就可以拉起来原生android 自定义的`HelloWorldActivity`。 
	
	这样就可以实现android 和 Rn 界面的互相调用。
	
###  PART3: Android 原生向 RN 发送数据 ###

通过底层原生`android`给`RN` 回调数据：实在找不到底层触发的合适时机，于是在RN 的界面点击一个按钮，调用底层，模拟底层有数据需要给Rn 回调，实际开发就免折腾了。

1.	在PART 1 第一步定义model 的时候，添加如下代码来模拟Android返回给RN 数据。
	
		 /*模拟android 有消息需要反馈到RN*/
	    @ReactMethod
	    public void sendDat2RN(){
	        Toast.makeText(getReactApplicationContext(),"send data to Rn",Toast.LENGTH_LONG).show();
	        sendDataToRn();
	    }
	
	    /* android 通过 RCTDeviceEventEmitter向 JS 发送数据*/
	    public void sendDataToRn(){
	        WritableMap writableMap = new WritableNativeMap();
	        String str = "{\"a\": \"A\",\"b\": \"B\"}";
	        writableMap.putString("data", str);
	        sendTransMisson(getReactApplicationContext(), "EventName", writableMap);
	    }
	
	    public void sendTransMisson(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
	        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
	                .emit(eventName, params);
	    }

	还是要注意，上层调用方法需要加`@ReactMethod` 的注解。
	
	在Rn 上随便搞个`TouchableOpacity`，触发一下

		sendDat2Rn(){
		  RNAndroid.sendDat2RN()
	    }
	于是模拟底层发送给RN 数据。
	
	接下来就是在RN 需要接收数据的地方进行接收了：
		
		componentWillMount() {
			DeviceEventEmitter.addListener('EventName', (msg)=> {
				console.log(msg);
				alert("msg = "+msg.data)
			});
		}
	注册监听 在底层 触发事件的名称`EventName`,就可以实现数据接收了。

---------------
最后来一个demo效果

![demo](https://i.imgur.com/6RC4Cj2.gif)

android拼错了。。懒得换图了，大家莫喷哈。。

## 如果有疑问或者更好的见解，欢迎拍砖 ##
	
	
	