package cc.yan.wings.client;

import cc.yan.wings.WingsMod;
import net.fabricmc.api.ClientModInitializer;

public final class WingsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FabricClientProxy proxy = new FabricClientProxy();
        WingsMod.instance().setProxy(proxy);
        proxy.initClient();
    }
}
