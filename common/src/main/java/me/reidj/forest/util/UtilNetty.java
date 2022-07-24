package me.reidj.forest.util;

import com.google.gson.Gson;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.SneakyThrows;
import me.reidj.forest.protocol.PackageWrapper;
import ru.cristalix.core.network.CorePackage;

/**
 * @author Рейдж 03.10.2021
 * @project ThePit
 */
public class UtilNetty {

    private static final Gson gson = new Gson();

    public static TextWebSocketFrame toFrame(CorePackage corePackage) {
        return new TextWebSocketFrame(gson.toJson(new PackageWrapper(corePackage.getClass().getName(), gson.toJson(corePackage))));
    }

    @SneakyThrows
    public static CorePackage readFrame(TextWebSocketFrame textFrame) {
        PackageWrapper wrapper = gson.fromJson(textFrame.text(), PackageWrapper.class);
        return (CorePackage) gson.fromJson(wrapper.getObjectData(), Class.forName(wrapper.getClazz()));
    }

}
