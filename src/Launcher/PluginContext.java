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

    private final FSRoot root;

    PluginContext(final FLCore.InstalledPlugin installedPlugin) {
        ip = installedPlugin;
        root = installedPlugin.root;
    }

    public final IImage getIcon() { return ip.getIcon(); }

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

    public final void addTaskGroup(final TaskGroup group) {
        synchronized (FLCore.groups) {
            FLCore.groups.add(group);
            FLCore.groups.notifyAll();
        }
    }

    public final FSRoot getPluginRoot() { return root; }
    public final File getPluginData() { return ip.data; }
    public final File getPluginCache() { return ip.cache; }

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
        }
    }
}