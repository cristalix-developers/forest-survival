package me.reidj.forest.handlers;

import io.netty.channel.Channel;
import ru.cristalix.core.network.CorePackage;

@FunctionalInterface
public interface PackageHandler<T extends CorePackage> {

	void handle(Channel channel, String serverName, T thePitPackage);

}
