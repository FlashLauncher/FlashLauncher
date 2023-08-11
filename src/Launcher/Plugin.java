package Launcher;

import Launcher.base.IAccount;
import Launcher.base.IMaker;
import Launcher.base.IProfile;
import UIL.base.IImage;
import Utils.FSRoot;

import java.io.File;

public class Plugin {
    private final PluginContext context;
    private final FLCore.InstalledPlugin ip;

    public Plugin(final PluginContext context) {
        this.context = context;
        ip = context.ip;
        context.plugin = this;
    }

    public final PluginContext getContext() { return context; }
    public IImage getIcon() { return ip.getIcon(); }
    public Market addMarket(final Market market) { return context.addMarket(market); }
    public boolean removeMarket(final Market market) { return context.removeMarket(market); }
    public IProfile addProfile(final IProfile profile) { return context.addProfile(profile); }
    public boolean removeProfile(final IProfile profile) { return context.removeProfile(profile); }
    public IAccount addAccount(final IAccount account) { return context.addAccount(account); }
    public boolean removeAccount(final IAccount account) { return context.removeAccount(account); }
    public void addAccountMaker(final IMaker<IAccount> account) { context.addAccountMaker(account); }
    public void addProfileMaker(final IMaker<IProfile> profile) { context.addProfileMaker(profile); }

    public void addTaskGroup(final TaskGroup group) { context.addTaskGroup(group); }

    public FSRoot getPluginRoot() { return context.getPluginRoot(); }
    public File getPluginData() { return context.getPluginData(); }
    public File getPluginCache() { return context.getPluginCache(); }

    /**
     * Called when the plugin is enabled.
     */
    public void onEnable() {}

    /**
     * Called when the plugin is disabled.
     */
    public void onDisable() {}

    /**
     * Called when the child plugin is enabled (before init plugin).
     */
    public void onPreEnableChild(final PluginContext childContext) {}

    /**
     * Called when the child plugin is enabled (after init plugin class or skipped init plugin class).
     */
    public void onEnableChild(final PluginContext childContext) {}

    /**
     * Called when the child plugin is disabled.
     */
    public void onPreDisableChild(final PluginContext childContext) {}

    /**
     * Called when the child plugin is disabled.
     */
    public void onDisableChild(final PluginContext childContext) {}
}