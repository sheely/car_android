package com.sky.base;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class SHIntent extends Intent{
    /**
     * Create an intent with a given action.  All other fields (data, type,
     * class) are null.  Note that the action <em>must</em> be in a
     * namespace because Intents are used globally in the system -- for
     * example the system VIEW action is android.intent.action.VIEW; an
     * application's custom action would be something like
     * com.google.app.myapp.CUSTOM_ACTION.
     *
     * @param action The Intent action, such as ACTION_VIEW.
     */
    public SHIntent(String action) {
        super(action);
    }

  /**
   * Create an intent with a given action and for a given data url.  Note
   * that the action <em>must</em> be in a namespace because Intents are
   * used globally in the system -- for example the system VIEW action is
   * android.intent.action.VIEW; an application's custom action would be
   * something like com.google.app.myapp.CUSTOM_ACTION.
   *
   * <p><em>Note: scheme and host name matching in the Android framework is
   * case-sensitive, unlike the formal RFC.  As a result,
   * you should always ensure that you write your Uri with these elements
   * using lower case letters, and normalize any Uris you receive from
   * outside of Android to ensure the scheme and host is lower case.</em></p>
   *
   * @param action The Intent action, such as ACTION_VIEW.
   * @param uri The Intent data URI.
   */
  public SHIntent(String action, Uri uri) {
    super(action,uri);
  }

  /**
   * Create an intent for a specific component.  All other fields (action, data,
   * type, class) are null, though they can be modified later with explicit
   * calls.  This provides a convenient way to create an intent that is
   * intended to execute a hard-coded class name, rather than relying on the
   * system to find an appropriate class for you; see {@link #setComponent}
   * for more information on the repercussions of this.
   *
   * @param packageContext A Context of the application package implementing
   * this class.
   * @param cls The component class that is to be used for the intent.
   *
   * @see #setClass
   * @see #setComponent
   * @see #Intent(String, android.net.Uri , Context, Class)
   */
  public SHIntent(Context packageContext, Class<?> cls) {
	   super(packageContext,cls);
	  }

  /**
   * Create an intent for a specific component with a specified action and data.
   * This is equivalent using {@link #Intent(String, android.net.Uri)} to
   * construct the Intent and then calling {@link #setClass} to set its
   * class.
   *
   * <p><em>Note: scheme and host name matching in the Android framework is
   * case-sensitive, unlike the formal RFC.  As a result,
   * you should always ensure that you write your Uri with these elements
   * using lower case letters, and normalize any Uris you receive from
   * outside of Android to ensure the scheme and host is lower case.</em></p>
   *
   * @param action The Intent action, such as ACTION_VIEW.
   * @param uri The Intent data URI.
   * @param packageContext A Context of the application package implementing
   * this class.
   * @param cls The component class that is to be used for the intent.
   *
   * @see #Intent(String, android.net.Uri)
   * @see #Intent(Context, Class)
   * @see #setClass
   * @see #setComponent
   */
  public SHIntent(String action, Uri uri,
          Context packageContext, Class<?> cls) {
     super(action,uri,packageContext,cls);
  }
  
  /**
   * Create an empty intent.
   */
  public SHIntent() {
  }

  /**
   * Copy constructor.
   */
  public SHIntent(SHIntent o) {
    super(o);
  }
  /**
   * Create an intent for a specific component.  All other fields (action, data,
   * type, class) are null, though they can be modified later with explicit
   * calls.  This provides a convenient way to create an intent that is
   * intended to execute a hard-coded class name, rather than relying on the
   * system to find an appropriate class for you; see {@link #setComponent}
   * for more information on the repercussions of this.
   *
   * @param packageContext A Context of the application package implementing
   * this class.
   * @param cls The component class that is to be used for the intent.
   *
   * @see #setClass
   * @see #setComponent
   * @see #Intent(String, android.net.Uri , Context, Class)
   */

}
