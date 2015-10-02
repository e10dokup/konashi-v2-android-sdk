package com.uxxu.konashi.test_all_functions;

import android.app.Activity;
import android.app.Fragment;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.uxxu.konashi.lib.js.KonashiJsService;

/**
 * Created by kiryu on 7/27/15.
 *
 * <pre>
 * <activity
 *  android:name=".activity.UriActivity"
 *  android:exported="true"
 *  android:screenOrientation="portrait">
 *
 *     <intent-filter>
 *         <action android:name="android.intent.action.VIEW"/>
 *         <category android:name="android.intent.category.DEFAULT"/>
 *         <category android:name="android.intent.category.BROWSABLE"/>
 *         <data android:scheme="@string/konashi_lib_js_schema"/>
 *     </intent-filter>
 * </activity>
 * <service android:name="com.uxxu.konashi.lib.js.KonashiJsService"/
 * </pre>
 */
public final class KonashiJsFragment extends Fragment {

    private static final String TAG = KonashiJsFragment.class.getSimpleName();

    public static final String TITLE = "KonashiJS";

    private ServiceConnection konashiJsServiceConnection;

    private WebView webView;

    private int counter = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(TITLE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_konashi_js, container, false);
        webView = (WebView) view.findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        webView.loadUrl("file:///android_asset/konashijs.html");

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        konashiJsServiceConnection = KonashiJsService.bindServiceTo(getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unbindService(konashiJsServiceConnection);
    }
}
