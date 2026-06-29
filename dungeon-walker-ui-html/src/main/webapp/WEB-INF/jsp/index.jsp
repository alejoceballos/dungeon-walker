<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>🚶🧱 Dungeon Walker 🧱 🚶‍➡️</title>
    <link href="/css/dungeon.css" rel="stylesheet">
    <script type="module" src="/js/dungeon-walker.js"></script>
</head>
<body>
<div id="title-pane" class="image-container show-border">
    <img src="/img/DW-LOGO.png" alt="Dungeon Walker Logo" width="10%" height="10%">
    <img src="/img/DW-Bricked-Title.png" alt="Dungeon Walker Title" width="20%" height="20%">
</div>
<div>
    <table id="main-pane-table" class="show-border">
        <tr>
            <th class="col-main show-border">Dungeon</th>
            <th class="col-secondary show-border">Messages</th>
        </tr>
        <tr>
            <td id="dungeon-pane" class="show-border">
                <table id="dungeon-table">
                    <tr>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                    </tr>
                    <tr>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                    </tr>
                    <tr>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                    </tr>
                    <tr>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                    </tr>
                    <tr>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                    </tr>
                    <tr>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                    </tr>
                    <tr>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                    </tr>
                    <tr>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                    </tr>
                    <tr>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                    </tr>
                    <tr>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                        <td>X</td>
                    </tr>
                </table>
            </td>
            <td id="messages-pane" class="show-border">
                <div id="scrollable-pane">
                    <ol id="messageList">
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                        <li>Message</li>
                    </ol>
                </div>
            </td>
        </tr>
    </table>
</div>
<div id="command-pane" class="show-border">
    <label for="command-input"> 🚶‍➡&gt️</label>
    <input type="text" id="command-input" placeholder="Enter command or 'h' for (help)">
</div>
<script>
    window.addEventListener("DOMContentLoaded", () => {
        window.initDungeonWalker(
            <c:out value="${messagesMaxCount}" />,
            '<c:out value="${securityProtocol}" />',
            '<c:out value="${securityHost}" />',
            '<c:out value="${securityEndpoint}" />',
            '<c:out value="${webSocketProtocol}" />',
            '<c:out value="${webSocketHost}" />',
            '<c:out value="${webSocketEndpoint}" />'
        );
    });
</script>
</body>
</html>