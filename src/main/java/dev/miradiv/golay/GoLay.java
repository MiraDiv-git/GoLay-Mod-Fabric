package dev.miradiv.golay;

import dev.miradiv.golay.network.LayPacket;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.api.ModInitializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GoLay implements ModInitializer
{
    public static final String MOD_ID = "GoLay";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Map<UUID, Boolean> layingPlayers = new HashMap<>(); //checking if player is laying (default should be false)
    @Override
    public void onInitialize()
    {
        LOGGER.info("GoLay mod by MiraDiv. Initializing...");

        PayloadTypeRegistry.playS2C().register(LayPacket.ID, LayPacket.CODEC);

        //Command should be written in this thing
        CommandRegistrationCallback.EVENT.register((commandDispatcher,
                                                     commandRegistryAccess,
                                                     registrationEnvironment) ->
        {
            //Command on laying
            commandDispatcher.register(CommandManager.literal("lay")
                    .executes(commandContext ->
                    {
                        ServerPlayerEntity player = commandContext.getSource().getPlayer();
                        if (player != null) //Checks if command is executed by player
                        {
                            //Checks if player is NOT laying
                            if (!layingPlayers.getOrDefault(player.getUuid(), false))
                            {
                                layingPlayers.put(player.getUuid(), true);
                                ServerPlayNetworking.send(player, new LayPacket(true));
                            }
                            else
                            {
                                commandContext.getSource().sendError(
                                        Text.literal("You already laying"));
                                return 0; //stops the execution here
                            }
                        }
                        else
                        {
                            commandContext.getSource().sendError(
                                    Text.literal("This command can be used only by player"));
                            return 0; //stops the execution here
                        }

                        return 1; //If everything is ok - it returns this as 'success'
                    }));

            //Command for stop laying
            commandDispatcher.register(CommandManager.literal("unlay")
                    .executes(commandContext ->
                    {
                        ServerPlayerEntity player = commandContext.getSource().getPlayer();
                        if (player != null)
                        {
                            //Checks if player IS laying
                            if (layingPlayers.getOrDefault(player.getUuid(), false))
                            {
                                layingPlayers.put(player.getUuid(), false);
                                ServerPlayNetworking.send(player, new LayPacket(false));
                            }
                            else
                            {
                                //Sends message to server if player is already standing
                                commandContext.getSource().sendError(
                                        Text.literal("You already standing"));
                                return 0; //stops the execution here
                            }
                        }
                        else
                        {
                            //Sends message to server if command is executed in server console
                            commandContext.getSource().sendError(
                                    Text.literal("This command can be used only by player"));
                            return 0; //stops the execution here
                        }

                        return 1; //If everything is ok - it returns this as 'success'
                    }));
        });

    }
}
