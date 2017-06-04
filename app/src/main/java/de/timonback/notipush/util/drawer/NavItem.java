package de.timonback.notipush.util.drawer;

public class NavItem {
    String mTitle;
    String mSubtitle;
    String mClassname;
    int mIcon;

    public NavItem(String title, String subtitle, String classname, int icon) {
        mTitle = title;
        mSubtitle = subtitle;
        mClassname = classname;
        mIcon = icon;
    }

    public String getClassname() {
        return mClassname;
    }

    public String getTitle() {
        return mTitle;
    }
}