package dev.miradiv.golay.client;

import dev.miradiv.golay.GoLay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class GoLayClient implements ClientModInitializer
{
    private static KeyBinding layKey;
    private static boolean wasKeyDown = false;

    @Override
    public void onInitializeClient()
    {
        layKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Lay",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                KeyBinding.Category.MOVEMENT
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client ->
        {
            boolean isKeyDown = layKey.isPressed();

            if (isKeyDown && !wasKeyDown)
            {
                if (client.player != null)
                {
                    if (GoLay.layingPlayers.getOrDefault(client.player.getUuid(), false))
                    {
                        client.player.networkHandler.sendChatCommand("unlay");
                    }
                    else
                    {
                        client.player.networkHandler.sendChatCommand("lay");
                    }
                }
            }

            wasKeyDown = isKeyDown;
        });
    }
}
