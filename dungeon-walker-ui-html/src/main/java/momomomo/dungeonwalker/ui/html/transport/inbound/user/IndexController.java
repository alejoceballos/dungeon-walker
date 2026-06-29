package momomomo.dungeonwalker.ui.html.transport.inbound.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.ui.html.transport.config.ViewProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class IndexController {

    private final ViewProperties viewProperties;

    @GetMapping
    public String index(final Model model) {
        log.debug("Loading index");

        model.addAttribute("messagesMaxCount", viewProperties.getMessagesMaxCount());
        model.addAttribute("securityProtocol", viewProperties.getSecurityProtocol());
        model.addAttribute("securityHost", viewProperties.getSecurityHost());
        model.addAttribute("securityEndpoint", viewProperties.getSecurityEndpoint());
        model.addAttribute("webSocketProtocol", viewProperties.getWebSocketProtocol());
        model.addAttribute("webSocketHost", viewProperties.getWebSocketHost());
        model.addAttribute("webSocketEndpoint", viewProperties.getWebSocketEndpoint());

        return "index";
    }

}
