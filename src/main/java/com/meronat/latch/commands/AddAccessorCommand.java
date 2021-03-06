/*
 * This file is part of Latch, licensed under the MIT License.
 *
 * Copyright (c) 2016-2018 IchorPowered <https://github.com/IchorPowered>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.meronat.latch.commands;

import com.google.common.collect.ImmutableSet;
import com.meronat.latch.Latch;
import com.meronat.latch.interactions.ChangeLockInteraction;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Set;
import java.util.UUID;

public class AddAccessorCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of(TextColors.RED, "You must be a player to use this command."));
        }

        final Player player = (Player) src;
        final Set<UUID> members = args.<User>getAll("add").stream().map(User::getUniqueId).collect(ImmutableSet.toImmutableSet());
        final ChangeLockInteraction addPlayers = new ChangeLockInteraction(player.getUniqueId());

        addPlayers.setPersistence(args.hasAny("p"));

        if (members.size() > 0) {
            if (members.contains(player.getUniqueId())) {
                throw new CommandException(Text.of(TextColors.RED, "You cannot add yourself as an accessor!"));
            }
            addPlayers.setMembersToAdd(members);
        } else {
            throw new CommandException(Text.of(TextColors.RED, "You must specify a user to add."));
        }

        Latch.getLockManager().setInteractionData(player.getUniqueId(), addPlayers);

        if (args.hasAny("p")) {
            player.sendMessage(Text.of(TextColors.DARK_GREEN, "You will add them on all locks you click until you type \"latch persist\"."));
        } else {
            player.sendMessage(Text.of(TextColors.DARK_GREEN, "You will add them on the next lock of yours you click."));
        }

        return CommandResult.success();
    }

}
