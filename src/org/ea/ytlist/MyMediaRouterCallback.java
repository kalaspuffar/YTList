package org.ea.ytlist;

import android.support.v7.media.MediaRouter;
import android.support.v7.media.MediaRouter.Callback;
import android.support.v7.media.MediaRouter.ProviderInfo;
import android.support.v7.media.MediaRouter.RouteInfo;
import android.util.Log;

public class MyMediaRouterCallback extends Callback {

	@Override
	public void onProviderAdded(MediaRouter router, ProviderInfo provider) {
		Log.d("MEDIA ROUTER", provider.toString());
		for(RouteInfo r : provider.getRoutes()) {
			Log.d("ROUTE", r.toString());		
		}
		super.onProviderAdded(router, provider);
	}

	@Override
	public void onProviderChanged(MediaRouter router, ProviderInfo provider) {
		// TODO Auto-generated method stub
		super.onProviderChanged(router, provider);
	}

	@Override
	public void onProviderRemoved(MediaRouter router, ProviderInfo provider) {
		// TODO Auto-generated method stub
		super.onProviderRemoved(router, provider);
	}

	@Override
	public void onRouteAdded(MediaRouter router, RouteInfo route) {
		// TODO Auto-generated method stub
		super.onRouteAdded(router, route);
	}

	@Override
	public void onRouteChanged(MediaRouter router, RouteInfo route) {
		// TODO Auto-generated method stub
		super.onRouteChanged(router, route);
	}

	@Override
	public void onRoutePresentationDisplayChanged(MediaRouter router,
			RouteInfo route) {
		// TODO Auto-generated method stub
		super.onRoutePresentationDisplayChanged(router, route);
	}

	@Override
	public void onRouteRemoved(MediaRouter router, RouteInfo route) {
		// TODO Auto-generated method stub
		super.onRouteRemoved(router, route);
	}

	@Override
	public void onRouteSelected(MediaRouter router, RouteInfo route) {
		// TODO Auto-generated method stub
		super.onRouteSelected(router, route);
	}

	@Override
	public void onRouteUnselected(MediaRouter router, RouteInfo route) {
		// TODO Auto-generated method stub
		super.onRouteUnselected(router, route);
	}

	@Override
	public void onRouteVolumeChanged(MediaRouter router, RouteInfo route) {
		// TODO Auto-generated method stub
		super.onRouteVolumeChanged(router, route);
	}
}
