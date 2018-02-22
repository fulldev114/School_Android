package com.xmpp.parent;

import android.os.Binder;

import java.lang.ref.WeakReference;

public class XmppServiceBinder<S> extends Binder {
	private final WeakReference<S> mService;

	public XmppServiceBinder(final S service) {
		mService = new WeakReference<S>(service);
	}

	public S getService() {
		return mService.get();
	}

}
