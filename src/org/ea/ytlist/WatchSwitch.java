package org.ea.ytlist;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class WatchSwitch implements MenuItem.OnMenuItemClickListener {

	private MainListAdapter mla = null;
	
	public WatchSwitch(MainListAdapter mla) {
		this.mla = mla;
	}
	
	@Override
	public boolean onMenuItemClick(MenuItem item) {
		if(item.getTitle().toString().equalsIgnoreCase("Watched")) {
			this.mla.setShowWatched(true);
			this.mla.notifyDataSetChanged();
			item.setTitle("New videos");			
		} else if(item.getTitle().toString().equalsIgnoreCase("New videos")) {
			this.mla.setShowWatched(false);
			this.mla.notifyDataSetChanged();
			item.setTitle("Watched");			
		}
		return true;
	}

}
