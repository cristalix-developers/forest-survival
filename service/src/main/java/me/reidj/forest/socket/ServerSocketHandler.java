package me.reidj.forest.socket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.AttributeKey;
import me.reidj.forest.ForestService;
import me.reidj.forest.protocol.GreetingPackage;
import me.reidj.forest.util.UtilNetty;
import ru.cristalix.core.network.CorePackage;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ServerSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

	private static final AttributeKey<String> serverInfoKey = AttributeKey.newInstance("serverinfo");

	public static final Map<String, Channel> connectedChannels = new ConcurrentHashMap<>();

	public static void broadcast(CorePackage pckg) {
		connectedChannels.values().forEach(channel -> send(channel, UtilNetty.toFrame(pckg)));
	}

	public static void send(Channel channel, CorePackage pckg) {
		send(channel, UtilNetty.toFrame(pckg));
	}

	public static void send(Channel channel, TextWebSocketFrame frame) {
		channel.writeAndFlush(frame, channel.voidPromise());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) {
		if (msg instanceof TextWebSocketFrame) {
			CorePackage thePitPackage = UtilNetty.readFrame((TextWebSocketFrame) msg);
			Channel channel = ctx.channel();
			if (thePitPackage instanceof GreetingPackage) {
				if (channel.hasAttr(serverInfoKey)) {
					System.out.println("Some channel tries to authorize, but it already in system!");
					return;
				}
				GreetingPackage pckg = (GreetingPackage) thePitPackage;
				if (connectedChannels.containsKey(pckg.getServerName())) {
					System.out.println("Channel want to register as " + pckg.getServerName() + ", but this name already in use!");
					ctx.close();
					return;
				}
				if (!pckg.getPassword().equals(ForestService.PASSWORD)) {
					System.out.println("Channel provided bad password: " + pckg.getPassword());
					if (channel.remoteAddress() instanceof InetSocketAddress) {
						System.out.println(channel.remoteAddress().toString());
					}
					ctx.close();
					return;
				}
				channel.attr(serverInfoKey).set(pckg.getServerName());
				connectedChannels.put(pckg.getServerName(), channel);
			} else {
				if (!channel.hasAttr(serverInfoKey)) {
					System.out.println("Some channel tries to send packet without authorization!");
					if (channel.remoteAddress() instanceof InetSocketAddress) {
						System.out.println(channel.remoteAddress().toString());
					}
					ctx.close();
					return;
				}
				String info = channel.attr(serverInfoKey).get();
				Optional.ofNullable(ForestService.HANDLER_MAP.get(thePitPackage.getClass()))
						.ifPresent(handler -> handler.handle(channel, info, thePitPackage));
			}
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		Channel channel = ctx.channel();
		if (channel.hasAttr(serverInfoKey)) {
			String name = channel.attr(serverInfoKey).get();
			connectedChannels.remove(name);
			System.out.println("Server disconnected! " + name);
		}
	}

}
