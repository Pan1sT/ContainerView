package me.pan1st.containerview.config;

import net.william278.annotaml.*;

@YamlFile
public class Setting {

    @YamlKey("setting.radius")
    public Integer radius = 16;

    @YamlKey("setting.cached-duration")
    @YamlComment("cached result duration, in seconds")
    public Integer cachedDuration = 60;

    @YamlKey("setting.checkPeriod")
    @YamlComment("async cache task period, in ticks")
    public Integer checkPeriod = 20;

    @YamlKey("message.command.reload")
    public String reload = "<grey> Configuration reloaded!";

    @YamlKey("message.command.no-permission")
    public String noPermission = "<red>You dont have permission to do that.";

    @YamlKey("message.command.invalid-argument")
    public String invalidArgument = "<red>Invalid command argument: <error>";

    @YamlKey("message.command.invalid-sender")
    public String invalidSender = "<red>Invalid command sender. You must be of type <gray><type></gray>.";

    @YamlKey("message.command.invalid-syntax")
    public String invalidSyntax = "<red>Invalid command syntax. Correct command syntax is: <syntax>";

    @YamlKey("message.query-not-found")
    public String queryNotFound = "<grey>There are no Containers found in range.";

    @YamlKey("message.container-not-found")
    public String containerNotFound = "<red>The container that you are inspecting is not found.";

    @YamlKey("message.no-cached-result")
    public String noCachedResult = "<red>You do not have any cached result";

    @YamlKey("message.invalid-page")
    public String invalidPage = "<red>Invalid Page";

    @YamlKey("message.result-header")
    public String resultHeader = "<grey>Here are the containers that around you:";

    @YamlKey("message.result-content")
    public String resultContent = "▣ <grey>Container Preview, <white><bold>X:<container_x> Y:<container_y> Z:<container_z></bold> <click_message>";

    @YamlKey("message.result-click-message")
    public String resultClickMessage = "<yellow>[Click to Preview]";

    @YamlKey("message.result-footer")
    public String resultFooter = "<result_previous> | page: <current_page> / <total_page> | <result_next> ";

    @YamlKey("message.result-footer-first")
    public String resultFooterFirst = "page: <current_page> / <total_page> | <result_next> ";

    @YamlKey("message.result-footer-last")
    public String resultFooterLast = "<result_previous> | page: <current_page> / <total_page>";

    @YamlKey("message.result-previous")
    public String resultPrevious = "<-- previous";

    @YamlKey("message.result-next")
    public String resultNext = "next -->";

    @YamlKey("message.gui-title")
    public String guiTitle = "<red>Previewing <container_type>";


    // You *must* include a zero-args constructor.
    public Setting() {
    }

}