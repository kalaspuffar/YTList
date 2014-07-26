package org.ea.ytlist;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.MediaRouteButton;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.support.v7.media.MediaRouter.RouteInfo;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;

public class ListSubscriptions extends Activity {

	private static final String TAG = "TAG";
	
	private MainListAdapter mla = null;
	private ListView layout = null;
	private VideoUpdater nu = null;
	
	private MediaRouter mMediaRouter;
	private MediaRouteSelector mMediaRouteSelector;
	private MediaRouter.Callback mMediaRouterCallback;
	private MediaRouteButton mMediaRouteButton;
	private CastDevice mSelectedDevice;
	private int mRouteCount = 0;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.mainactivity);
        
        layout = (ListView)findViewById(R.id.mainList);
        if(mla == null) mla = new MainListAdapter(this, new String[] {""});		
		mla.setNotifyOnChange(true);
		layout.setAdapter(mla);

        ImageView iv = (ImageView)findViewById(R.id.reloadImage);
        iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				nu = new VideoUpdater(ListSubscriptions.this, mla);
            	nu.execute();
			}        	
        });		
        
		mMediaRouter = MediaRouter.getInstance(getApplicationContext());
		// Create a MediaRouteSelector for the type of routes your app supports
		mMediaRouteSelector = new MediaRouteSelector.Builder()
				.addControlCategory(CastMediaControlIntent.categoryForCast(CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID)).build();
		// Create a MediaRouter callback for discovery events
		mMediaRouterCallback = new MyMediaRouterCallback();
		
		// Set the MediaRouteButton selector for device discovery.
		mMediaRouteButton = (MediaRouteButton) findViewById(R.id.media_route_button);
		mMediaRouteButton.setRouteSelector(mMediaRouteSelector);
        
        
		if(savedInstanceState == null && this.nu == null) {
			this.nu = new VideoUpdater(this, mla);
			this.nu.execute();     
		}
    }

    @Override
	protected void onPause() {
		mMediaRouter.removeCallback(mMediaRouterCallback);
		super.onPause();
    	if(nu != null) {
			nu.cancel(true);
	    	nu = null;
    	}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback, MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);
	}
	
	@Override
	protected void onStart() {
	    super.onStart();
	    mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
	            MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);
	}

	@Override
	protected void onStop() {
	    mMediaRouter.removeCallback(mMediaRouterCallback);
	    super.onStop();
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_subscriptions, menu);
        
        for(int i = 0; i < menu.size(); i++) {
            if(menu.getItem(i).getTitle().toString().equalsIgnoreCase("Watched")) {
            	menu.getItem(i).setOnMenuItemClickListener(new WatchSwitch(this.mla));
            }        	
        }
        
        return true;
    }
    
	private class MyMediaRouterCallback extends MediaRouter.Callback {
		@Override
		public void onRouteAdded(MediaRouter router, RouteInfo route) {
			Log.d(TAG, "onRouteAdded");
			if (++mRouteCount == 1) {
				// Show the button when a device is discovered.
				mMediaRouteButton.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onRouteRemoved(MediaRouter router, RouteInfo route) {
			Log.d(TAG, "onRouteRemoved");
			if (--mRouteCount == 0) {
				// Hide the button if there are no devices discovered.
				mMediaRouteButton.setVisibility(View.GONE);
			}
		}

		@Override
		public void onRouteSelected(MediaRouter router, RouteInfo info) {
			Log.d(TAG, "onRouteSelected");
			mSelectedDevice = CastDevice.getFromBundle(info.getExtras());

			Toast.makeText(ListSubscriptions.this, "Todo connect: "+mSelectedDevice.getFriendlyName(), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onRouteUnselected(MediaRouter router, RouteInfo info) {
			Log.d(TAG, "onRouteUnselected: info=" + info);
			mSelectedDevice = null;
		}
	}

}
