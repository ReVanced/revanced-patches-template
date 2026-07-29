package app.revanced.extension.dcinside.patches.hook.json;

import org.jetbrains.annotations.NotNull;

public abstract class BaseJsonHook implements JsonHook {
    /**
     * Abstract method to be implemented by subclasses to modify the String.
     *
     * @param json The String to modify.
     */
    public abstract String apply(@NotNull String json);

    @Override
    @NotNull
    public String transform(@NotNull String json) {
        return apply(json);
    }
}
