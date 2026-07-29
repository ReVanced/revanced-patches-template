package app.revanced.extension.dcinside.patches.hook.json;

import app.revanced.extension.dcinside.patches.hook.patch.Hook;
import org.jetbrains.annotations.NotNull;

public interface JsonHook extends Hook<String> {
    /**
     * Transform a String.
     *
     * @param json The String.
     * @return The transformed String.
     */
    @NotNull
    String transform(@NotNull String json);

    @Override
    @NotNull
    default String hook(@NotNull String type) {
        return transform(type);
    }
}