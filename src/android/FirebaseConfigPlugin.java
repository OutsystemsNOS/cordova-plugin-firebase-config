package by.chemerisuk.cordova.firebase;

import static com.google.android.gms.tasks.Tasks.await;
import static by.chemerisuk.cordova.support.ExecutionThread.WORKER;

import java.util.Collections;

import android.content.Context;
import android.util.Log;

import by.chemerisuk.cordova.support.CordovaMethod;
import by.chemerisuk.cordova.support.ReflectiveCordovaPlugin;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONArray;


public class FirebaseConfigPlugin extends ReflectiveCordovaPlugin {
    private static final String TAG = "FirebaseConfigPlugin";

    private FirebaseRemoteConfig firebaseRemoteConfig;

    @Override
    protected void pluginInitialize() {
        Log.d(TAG, "Starting Firebase Remote Config plugin");

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance(); 

        String filename = preferences.getString("FirebaseRemoteConfigDefaults", "");
        if (filename.isEmpty()) {
            // always call setDefaults in order to avoid exception
            // https://github.com/firebase/quickstart-android/issues/291
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(3600).build();
            firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
            firebaseRemoteConfig.setDefaultsAsync(Collections.<String, Object>emptyMap());
        } else {
            Context ctx = cordova.getActivity().getApplicationContext();
            int resourceId = ctx.getResources().getIdentifier(filename, "xml", ctx.getPackageName());
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(3600).build();
            firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
            firebaseRemoteConfig.setDefaultsAsync(resourceId);
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    	if (action.equals("getValue")) {
    		if (args.length() > 1) {
    			this.getValue(callbackContext, args.getString(0), args.getString(1));
    		} else {
    			this.getValue(callbackContext, args.getString(0), null);
    		}
    		return true;
    	}
    	return false;
    }
    
    private void getValue(final CallbackContext callbackContext, final String key, final String namespace) {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
            try {
                FirebaseRemoteConfigValue value = namespace == null
                        ? FirebaseRemoteConfig.getInstance().getValue(key)
                        : FirebaseRemoteConfig.getInstance().getValue(key, namespace);
                callbackContext.success(value.asString());
            } catch (Exception e) {
                callbackContext.error(e.getMessage());
            }
        }
      });
    }
    
    @CordovaMethod(WORKER)
    protected void fetch(CordovaArgs args, CallbackContext callbackContext) throws Exception {
        long expirationDuration = args.getLong(0);
        await(firebaseRemoteConfig.fetch(expirationDuration));
        callbackContext.success();
    }

    @CordovaMethod(WORKER)
    protected void activate(CallbackContext callbackContext) throws Exception {
        boolean activated = await(firebaseRemoteConfig.activate());
        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, activated));
    }

    @CordovaMethod(WORKER)
    protected void fetchAndActivate(CallbackContext callbackContext) throws Exception {
        boolean activated = await(firebaseRemoteConfig.fetchAndActivate());
        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, activated));
    }

    @CordovaMethod
    protected void getBoolean(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        String key = args.getString(0);
        callbackContext.sendPluginResult(
                new PluginResult(PluginResult.Status.OK, getValue(key).asBoolean()));
    }

    @CordovaMethod
    protected void getBytes(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        String key = args.getString(0);
        callbackContext.success(getValue(key).asByteArray());
    }

    @CordovaMethod
    protected void getNumber(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        String key = args.getString(0);
        callbackContext.sendPluginResult(
                new PluginResult(PluginResult.Status.OK, (float)getValue(key).asDouble()));
    }

    @CordovaMethod
    protected void getString(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        String key = args.getString(0);
        callbackContext.success(getValue(key).asString());
    }

    @CordovaMethod
    protected void getValueSource(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        String key = args.getString(0);
        callbackContext.success(getValue(key).getSource());
    }

    private FirebaseRemoteConfigValue getValue(String key) {
        return firebaseRemoteConfig.getValue(key);
    }
}
