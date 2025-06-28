package me.axieum.mcmod.minecord.impl.chat.callback.minecraft;

import java.util.Map;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;

import me.axieum.mcmod.minecord.api.Minecord;
import me.axieum.mcmod.minecord.api.util.PlaceholdersExt;
import me.axieum.mcmod.minecord.api.util.StringUtils;
import me.axieum.mcmod.minecord.impl.chat.util.DiscordDispatcher;
import me.axieum.mcmod.minecord.mixin.chat.LivingEntityAccessor;
import static me.axieum.mcmod.minecord.api.util.PlaceholdersExt.string;

/**
 * A listener for when a Minecraft player changes world.
 */
public class PlayerChangeWorldCallback implements ServerEntityWorldChangeEvents.AfterPlayerChange
{
    @Override
    public void afterChangeWorld(ServerPlayerEntity player, ServerWorld origin, ServerWorld dest)
    {
        Minecord.getInstance().getJDA().ifPresent(jda -> {
            final BlockPos lastBlockPos = ((LivingEntityAccessor) player).getLastBlockPos();

            // Skip if no last known block position is available
            if (lastBlockPos == null) return;

            /*
             * Prepare the message placeholders.
             */

            final PlaceholderContext ctx = PlaceholderContext.of(player);
            final Map<String, PlaceholderHandler> placeholders = Map.of(
                // The name of the world the player entered
                "world", string(StringUtils.getWorldName(dest)),
                // The X coordinate of where the player entered
