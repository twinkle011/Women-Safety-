package com.shesafe.ui;

import com.shesafe.SOSHistoryScreen;
import com.shesafe.model.User;

/**
 * SOSHistoryFrame
 * Compatibility adapter residing in 'com.shesafe.ui' package.
 * Delegates to SOSHistoryScreen.
 */
public class SOSHistoryFrame extends SOSHistoryScreen {

    public SOSHistoryFrame() {
        super();
    }

    public SOSHistoryFrame(User user) {
        super(user);
    }
}
