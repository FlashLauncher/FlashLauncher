package Launcher;

import Launcher.base.IAccount;
import Launcher.base.IMaker;
import Launcher.base.IProfile;
import UIL.base.IImage;
import Utils.FSRoot;

import java.io.File;
import java.util.ArrayList;

public class PluginContext {
    private final Object o = new Object();
    final FLCore.InstalledPlugin ip;
    Plugin plugin = null;
    boolean enabled = false;

    private final ArrayList<Market> markets = new ArrayList<>();
    private final ArrayList<IProfile> profiles = new ArrayList<>();
    private final ArrayList<IAccount> accounts = new ArrayList<>();
    private final ArrayList<IMaker<IProfile>> profileMakers = new ArrayList<>();
    private final ArrayList<IMaker<IAccount>> accountMakers = new ArrayList<>();
    private final ArrayList<FLMenuItemListener>
            menu_items = new ArrayList<>(),
            settings_items = new ArrayList<>(),
            help_items = new ArrayList<>()
    ;

    private final FSRoot root;

    PluginContext(final FLCore.InstalledPlugin installedPlugin) {
        ip = installedPlugin;
        root = installedPlugin.root;
    }

    public final IImage getIcon() { return ip.getIcon(); }
    public final Plugin getPlugin() { return plugin; }
    public final FSRoot getPluginRoot() { return root; }
    public final File getPluginData() { return ip.data; }
    public final File getPluginCache() { return ip.cache; }

    public final PluginContext getContext(final String id) {
        synchronized (FLCore.installed) {
            for (final FLCore.InstalledMeta ip : FLCore.installed)
                if (ip.getID().equals(id))
                    if (ip instanceof FLCore.InstalledPlugin && ((FLCore.InstalledPlugin) ip).enabled)
                        return ((FLCore.InstalledPlugin) ip).context;
                    else
                        return null;
        }
        return null;
    }

    public final Plugin getPlugin(final String id) {
        synchronized (FLCore.installed) {
            for (final FLCore.InstalledMeta ip : FLCore.installed)
                if (ip.getID().equals(id))
                    if (ip instanceof FLCore.InstalledPlugin && ((FLCore.InstalledPlugin) ip).enabled)
                        return ((FLCore.InstalledPlugin) ip).plugin;
                    else
                        return null;
        }
        return null;
    }

    public final PluginContext getConnectedContext(final String id) {
        synchronized (ip.c) {
            for (final FLCore.InstalledPlugin ip : ip.connected)
                if (ip.getID().equals(id))
                    if (ip.enabled)
                        return ip.context;
                    else
                        return null;
        }
        return null;
    }

    public final Market addMarket(final Market market) {
        synchronized (markets) {
            markets.add(market);
        }
        synchronized (o) {
            if (enabled)
                synchronized (FLCore.markets) {
                    FLCore.markets.add(market);
                    FLCore.markets.notifyAll();
                }
        }
        return market;
    }

    public final boolean removeMarket(final Market market) {
        synchronized (o) {
            if (enabled)
                synchronized (FLCore.markets) {
                    FLCore.markets.remove(market);
                    FLCore.markets.notifyAll();
                }
        }
        synchronized (markets) {
            return markets.remove(market);
        }
    }

    public final IProfile addProfile(final IProfile profile) {
        synchronized (profiles) {
            profiles.add(profile);
        }
        synchronized (o) {
            if (enabled)
                synchronized (FLCore.profiles) {
                    FLCore.profiles.add(profile);
                    FLCore.profiles.notifyAll();
                }
        }
        return profile;
    }

    public final boolean removeProfile(final IProfile profile) {
        synchronized (o) {
            if (enabled)
                synchronized (FLCore.profiles) {
                    FLCore.profiles.remove(profile);
                    FLCore.profiles.notifyAll();
                }
        }
        synchronized (profiles) {
            return profiles.remove(profile);
        }
    }

    public final IAccount addAccount(final IAccount account) {
        synchronized (accounts) {
            accounts.add(account);
        }
        synchronized (o) {
            if (enabled)
                synchronized (FLCore.accounts) {
                    FLCore.accounts.add(account);
                    FLCore.accounts.notifyAll();
                }
        }
        return account;
    }

    public final boolean removeAccount(final IAccount account) {
        synchronized (o) {
            if (enabled)
                synchronized (FLCore.accounts) {
                    FLCore.accounts.remove(account);
                    FLCore.accounts.notifyAll();
                }
        }
        synchronized (accounts) {
            return accounts.remove(account);
        }
    }

    public final void addProfileMaker(final IMaker<IProfile> maker) {
        synchronized (profileMakers) {
            profileMakers.add(maker);
        }
        synchronized (o) {
            if (enabled)
                synchronized (FLCore.profileMakers) {
                    FLCore.profileMakers.add(maker);
                    FLCore.profileMakers.notifyAll();
                }
        }
    }

    public final void addAccountMaker(final IMaker<IAccount> maker) {
        synchronized (accountMakers) {
            accountMakers.add(maker);
        }
        synchronized (o) {
            if (enabled)
                synchronized (FLCore.accountMakers) {
                    FLCore.accountMakers.add(maker);
                    FLCore.accountMakers.notifyAll();
                }
        }
    }

    public final void addMenuItem(final FLMenuItemListener listener) {
        synchronized (menu_items) {
            menu_items.add(listener);
        }
        synchronized (o) {
            if (enabled)
                synchronized (FLCore.MENU_ITEMS) {
                    FLCore.MENU_ITEMS.add(listener);
                    FLCore.MENU_ITEMS.notifyAll();
                }
        }
    }

    public final void addSettingsItem(final FLMenuItemListener listener) {
        synchronized (settings_items) {
            settings_items.add(listener);
        }
        synchronized (o) {
            if (enabled)
                synchronized (FLCore.SETTINGS_ITEMS) {
                    FLCore.SETTINGS_ITEMS.add(listener);
                    FLCore.SETTINGS_ITEMS.notifyAll();
                }
        }
    }

    public final void addHelpItem(final FLMenuItemListener listener) {
        synchronized (help_items) {
            help_items.add(listener);
        }
        synchronized (o) {
            if (enabled)
                synchronized (FLCore.HELP_ITEMS) {
                    FLCore.HELP_ITEMS.add(listener);
                    FLCore.HELP_ITEMS.notifyAll();
                }
        }
    }

    public final void addTaskGroup(final TaskGroup group) {
        synchronized (FLCore.groups) {
            FLCore.groups.add(group);
            FLCore.groups.notifyAll();
        }
    }

    final void onEnable() {
        synchronized (o) {
            enabled = true;
            synchronized (FLCore.markets) {
                synchronized (markets) {
                    synchronized (markets) {
                        FLCore.markets.addAll(markets);
                    }
                }
                FLCore.markets.notifyAll();
            }
            synchronized (FLCore.profiles) {
                synchronized (profiles) {
                    FLCore.profiles.addAll(profiles);
                }
                FLCore.profiles.notifyAll();
            }
            synchronized (FLCore.accounts) {
                synchronized (accounts) {
                    FLCore.accounts.addAll(accounts);
                }
                FLCore.accounts.notifyAll();
            }
            synchronized (FLCore.profileMakers) {
                synchronized (profileMakers) {
                    FLCore.profileMakers.addAll(profileMakers);
                }
                FLCore.profileMakers.notifyAll();
            }
            synchronized (FLCore.accountMakers) {
                synchronized (accountMakers) {
                    FLCore.accountMakers.addAll(accountMakers);
                }
                FLCore.accountMakers.notifyAll();
            }
            synchronized (FLCore.MENU_ITEMS) {
                synchronized (menu_items) {
                    FLCore.MENU_ITEMS.addAll(menu_items);
                }
                FLCore.MENU_ITEMS.notifyAll();
            }
            synchronized (FLCore.SETTINGS_ITEMS) {
                synchronized (settings_items) {
                    FLCore.SETTINGS_ITEMS.addAll(settings_items);
                }
                FLCore.SETTINGS_ITEMS.notifyAll();
            }
            synchronized (FLCore.HELP_ITEMS) {
                synchronized (help_items) {
                    FLCore.HELP_ITEMS.addAll(help_items);
                }
                FLCore.HELP_ITEMS.notifyAll();
            }
        }
    }

    final void onDisable() {
        synchronized (o) {
            enabled = false;
            synchronized (FLCore.markets) {
                synchronized (markets) {
                    FLCore.markets.removeAll(markets);
                }
                FLCore.markets.notifyAll();
            }
            synchronized (FLCore.profiles) {
                synchronized (profiles) {
                    FLCore.profiles.removeAll(profiles);
                }
                FLCore.profiles.notifyAll();
            }
            synchronized (FLCore.accounts) {
                synchronized (accounts) {
                    FLCore.accounts.removeAll(accounts);
                }
                FLCore.accounts.notifyAll();
            }
            synchronized (FLCore.profileMakers) {
                synchronized (profileMakers) {
                    FLCore.profileMakers.removeAll(profileMakers);
                }
                FLCore.profileMakers.notifyAll();
            }
            synchronized (FLCore.accountMakers) {
                synchronized (accountMakers) {
                    FLCore.accountMakers.removeAll(accountMakers);
                }
                FLCore.accountMakers.notifyAll();
            }
            synchronized (FLCore.MENU_ITEMS) {
                synchronized (menu_items) {
                    FLCore.MENU_ITEMS.removeAll(menu_items);
                }
                FLCore.MENU_ITEMS.notifyAll();
            }
            synchronized (FLCore.SETTINGS_ITEMS) {
                synchronized (settings_items) {
                    FLCore.SETTINGS_ITEMS.removeAll(settings_items);
                }
                FLCore.SETTINGS_ITEMS.notifyAll();
            }
            synchronized (FLCore.HELP_ITEMS) {
                synchronized (help_items) {
                    FLCore.HELP_ITEMS.removeAll(help_items);
                }
                FLCore.HELP_ITEMS.notifyAll();
            }
        }
    }
}