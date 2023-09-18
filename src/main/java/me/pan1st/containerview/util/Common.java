package me.pan1st.containerview.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.List;
import java.util.stream.Collectors;

public class Common {

    public static Component deserialize(final String message) {
        return MiniMessage.miniMessage().deserialize(message).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public static List<Component> deserialize(final List<String> stringList) {
        return stringList.stream() // Process in parallel
                .map(MiniMessage.miniMessage()::deserialize)
                .map(message -> message.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))// Convert each string using miniMessage function
                .collect(Collectors.toList()); // Collect the results into a list
    }

    public static Component withPlaceholder(final String message, final TagResolver... placeholders) {
        return MiniMessage.miniMessage().deserialize(message, placeholders);
    }
}