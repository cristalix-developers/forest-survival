import me.reidj.forest.AppKt;
import me.reidj.forest.protocol.SaveUserPackage;
import me.reidj.forest.protocol.StatPackage;
import me.reidj.forest.user.DefaultElements;
import me.reidj.forest.user.User;
import org.bukkit.event.Listener;
import ru.cristalix.core.CoreApi;
import ru.cristalix.core.event.AccountEvent;
import stat.Stat;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author : Рейдж
 * @project : forest
 **/
public class PlayerDataManager implements Listener {

    public PlayerDataManager() {
        CoreApi api = CoreApi.get();

        api.bus().register(this, AccountEvent.Load.class, event -> {
            if (event.isCancelled())
                return;
            UUID uuid = event.getUuid();
            try {
                StatPackage stat = AppKt.getClientSocket().writeAndAwaitResponse(new StatPackage(uuid)).get(5L, TimeUnit.SECONDS);
                Stat userInfo = stat.getStat();
                if (userInfo == null) userInfo = DefaultElements.INSTANCE.createNewUser(uuid);
                AppKt.getApp().getUserMap().put(uuid, new User(userInfo));
            } catch (Exception ex) {
                event.setCancelReason("Не удалось загрузить статистику.");
                event.setCancelled(true);
                ex.printStackTrace();
            }
        }, 400);
        api.bus().register(this, AccountEvent.Unload.class, event -> {
            User user = AppKt.getApp().getUserMap().remove(event.getUuid());
            if (user == null)
                return;
            Stat info = user.getStat();
            AppKt.getClientSocket().write(new SaveUserPackage(event.getUuid(), info));
        }, 100);
    }
}
