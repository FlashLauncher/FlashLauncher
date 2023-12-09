package Launcher;

import Launcher.base.IAccount;
import Launcher.base.IMaker;
import Launcher.base.IProfile;
import UIL.base.IImage;
import Utils.FSRoot;
import Utils.Version;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Plugin {
    private final PluginContext context;
    private final InstalledPlugin ip;

    public Plugin(final PluginContext context) {
        this.context = context;
        ip = context.ip;
        context.plugin = this;
    }

    /**
     * @since FlashLauncher 0.2.1
     */
    public Version getVersion() { return context.getVersion(); }

    public FSRoot getPluginRoot() { return context.getPluginRoot(); }
    public File getPluginData() { return context.getPluginData(); }
    public File getPluginCache() { return context.getPluginCache(); }
    public final PluginContext getContext() { return context; }
    public PluginContext getContext(final String id) { return context.getContext(id); }
    public PluginContext getConnectedContext(final String id) { return context.getConnectedContext(id); }
    public IImage getIcon() { return ip.getIcon(); }
    protected Market addMarket(final Market market) { return context.addMarket(market); }
    protected boolean removeMarket(final Market market) { return context.removeMarket(market); }
    protected ConcurrentLinkedQueue<IProfile> getProfiles() { return context.getProfiles(); }
    protected IProfile addProfile(final IProfile profile) { return context.addProfile(profile); }
    protected boolean removeProfile(final IProfile profile) { return context.removeProfile(profile); }
    protected ConcurrentLinkedQueue<IAccount> getAccounts() { return context.getAccounts(); }
    protected IAccount addAccount(final IAccount account) { return context.addAccount(account); }
    protected boolean removeAccount(final IAccount account) { return context.removeAccount(account); }
    protected void addAccountMaker(final IMaker<IAccount> account) { context.addAccountMaker(account); }
    protected void addProfileMaker(final IMaker<IProfile> profile) { context.addProfileMaker(profile); }
    protected void addMenuItem(final FLMenuItemListener listener) { context.addMenuItem(listener); }
    protected void addSettingsItem(final FLMenuItemListener listener) { context.addSettingsItem(listener); }
    protected void addHelpItem(final FLMenuItemListener listener) { context.addHelpItem(listener); }

    protected void addTaskGroup(final TaskGroup group) { context.addTaskGroup(group); }

    /**
     * Called when the plugin is enabled.
     */
    protected void onEnable() {}

    /**
     * Called when the plugin is disabled.
     */
    protected void onDisable() {}

    /**
     * Called when the child plugin is enabled (before init plugin).
     */
    protected void onPreEnableChild(final PluginContext childContext) {}

    /**
     * Called when the child plugin is enabled (after init plugin class or skipped init plugin class).
     */
    protected void onEnableChild(final PluginContext childContext) {}

    /**
     * Called when the child plugin is disabled.
     */
    protected void onPreDisableChild(final PluginContext childContext) {}

    /**
     * Called when the child plugin is disabled.
     */
    protected void onDisableChild(final PluginContext childContext) {}
}