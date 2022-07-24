package me.reidj.forest.protocol;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import ru.cristalix.core.network.CorePackage;
import stat.Stat;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public class SaveUserPackage extends CorePackage {

    // request
    private final UUID user;
    private final Stat stat;

    // no response

}
