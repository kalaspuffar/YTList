package org.ea.ytlist;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.media.MediaControlIntent;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;

public class ListSubscriptions extends Activity {

	private MainListAdapter mla = null;
	private ListView layout = null;
	private VideoUpdater nu = null;
	
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
        
        
        MediaRouteSelector mediaSelector = new MediaRouteSelector.Builder()
        .addControlCategory(MediaControlIntent.CATEGORY_LIVE_VIDEO)
        .addControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
        .build();

        
        
		MediaRouter router = MediaRouter.getInstance(this.getApplicationContext());
		router.addCallback(mediaSelector, new MyMediaRouterCallback(), MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);

        
		if(savedInstanceState == null && this.nu == null) {
			this.nu = new VideoUpdater(this, mla);
			this.nu.execute();     
		}
    }

    @Override
	protected void onPause() {
		super.onPause();
    	if(nu != null) {
			nu.cancel(true);
	    	nu = null;
    	}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}	
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_subscriptions, menu);
        return true;
    }
    
}
