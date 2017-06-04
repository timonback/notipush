package de.timonback.notipush;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends Fragment {
    public static final String TAG = AboutFragment.class.getSimpleName();
    public static final String GITHUB_URL = "github.com/timonback/notipush";

    public static Pair<String, Integer> getAppVersionAndBuild(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return new Pair<String, Integer>(pInfo.versionName, pInfo.versionCode);
        } catch (Exception e) {
            Log.e(TAG, "Could not get version number");
            return new Pair<String, Integer>("", 0);
        }
    }

    @SuppressLint("DefaultLocale")
    public static boolean launchWebBrowser(Context context, String url) {
        try {
            url = url.toLowerCase();
            if (!url.startsWith("http://") || !url.startsWith("https://")) {
                url = "http://" + url;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            if (null == resolveInfo) {
                Log.e(TAG, "No activity to handle web intent");
                return false;
            }
            context.startActivity(intent);
            Log.i(TAG, "Launching browser with url: " + url);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Could not start web browser", e);
            return false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        TextView version = (TextView) rootView.findViewById(R.id.version);
        version.setText(getAppVersionAndBuild(getActivity()).first);

        TextView gotToGithub = (TextView) rootView.findViewById(R.id.go_to_github);
        gotToGithub.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                launchWebBrowser(getActivity(), GITHUB_URL);

            }
        });

        return rootView;
    }
}