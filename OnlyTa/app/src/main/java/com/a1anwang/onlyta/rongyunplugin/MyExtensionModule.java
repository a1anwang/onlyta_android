package com.a1anwang.onlyta.rongyunplugin;

import java.util.List;

import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation;

/**
 * Created by a1anwang.com on 2018/1/24.
 */

public class MyExtensionModule extends DefaultExtensionModule {
    private MyLocationPlugin myPlugin;
    List<IPluginModule> pluginModules;

    @Override
    public List<IPluginModule> getPluginModules(Conversation.ConversationType conversationType) {
        if(pluginModules==null){
            pluginModules=  super.getPluginModules(conversationType);
            myPlugin=new MyLocationPlugin();
            pluginModules.add(myPlugin);
        }
        return pluginModules;
    }
}
