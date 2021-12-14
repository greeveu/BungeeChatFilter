## BungeeChatFilter

**WARNING: This isnt a drop in replacement! The config and the way you need to set certian values has changed!**

__Changes:__
- Removed `Monitor Commands` and `Commands`. You do not have to specify which commands you want to monitor. You can from now on simply monitor all of them. To ignore them just dont write regex which matches.
- Its no longer possible to define a regex over multiple lines. Just use a `|` to combine the regex.
- Removed the `AntiSpam` and `AntiRepeat` function. Feel free to use another plugin like 2ls chatsentinel for that. (or make a PR to add a good AntiSpam and AntiRepeat)
+ You may define a `server` field per rule. This has to be a regex and the actions will only execute if the regex finds a match with the server name. If its ommited it will not check the server name.
+ You may use the `permissiontype` field to define how a permission check should work. You may either have a bypass permission or a required permission. Default is: `BYPASS`
+ You can add `ignoreCommands: true` if you want a rule to ignore commands.
+ You can use `{player}`, `{message}` and `{arguments}` in the `pcommand`, `scommand` and `ccommand` message

```yaml
##########################################
##                                      ##
##      Bungee Chat Filter Config       ##
##               v2.0                   ##
##########################################

#Rules - Groups of rules which monitor the chat
#rules:
#   rulename:  - the name of the rule
#       regex:  - the string regex that the rule will check for
#       ignores:  - If the message contains a value that matches ignore then none of the actions will be performed
#       permission: - the permission required to bypass or use this rule
#       permissiontype: - either "BYPASS" or "REQUIRED" depending on how the permission should the used
#       server: - a regex to define on which servers a rule is valid
#       actions: - here is the list of actions the rule will perform if matched
#           deny: true      - this will deny the message and cancel the event
#           message: (message)      -this will send a message (message) to the player
#           kick: (message)     - this will kick the player with the (message)
#           alert: (message)        - this will send a broadcast to the server {player} will be replaced with the players display name
#           command: /(command)     - this will cause the player to send the (command)
#           remove: true        - this will remove any matches from the players message
#           replace:              - this will replace the matched word with a random word from the list below
#               - word1
#               - word2
#           lower:            - this will change any matches into lowercase
#          pcommand: /(command)    - Proxy command, this will cause the player to send the (command) to the proxy server
#          scommand: /(command)    - Server command this will cause the player to send the (command) to their current server
#          ccommand: /(command)    - Console command, this will cause the proxy server console to execute the (command).
#                                      Note that there is currently no way to execute a command as the current server console.


rules:

#Filters the word fuck and replaces it with a random word from the list
    swearfilter1:
        regex: (?i)(f+u+c+k+|f+u+k+|f+v+c+k+|f+u+q+)
        actions:
            replace:
                - fudge
                - frack
                - funk
                - fork

#Filters the word nigger and denys the chat even while kicking the player and alerting the server why the player was kicked
    swearfilter3:
        regex: "n[^a]gg+(a|er|uh)"
        actions:
            deny: true
            alert: "{player} has been kicked for racism"
            kick: "You have been kicked for racism"

#Replaces the word bloodsplat with a coloured word if they have permission for colours and a chat plugin on the server that supports colors.
    colorReplace:
        regex: "bloodsplat"
        actions:
            replace:
                - "&cBloodsplat"
                #Will only work if the player has permission for colors on the servers chat plugin.


#when a player asks for op they will instead say one of the following from the list and be sent a message
    askforOP:
        regex: "(?i).*give me op.*|.*can i have op.*"
        actions:
            replace:
                - Can you please ban me
                - I use xray!
            message: '&cPlease don''t ask for OP'

#When the player uses 4 or more letters in a row all in caps lock then the message will be changed to lowercase
    CAPSPAM:
        regex: (\p{Lu}|\s){4,}
        actions:
            lower: true

#Removes magic, bold and italics from the players chat when they dont have the bungeefilter.antiformatbypass permission.
    AntiFormatText:
        regex: '&[k-o]'
        permission: bungeefilter.antiformatbypass
        server: (?i)bedwars.*
        actions:
            remove: true
            message: '&cYou do not have permission to use Format text'

#Removes color from the chat unless the player has the bungeefilter.colorbypass permission
    AntiColorText:
        regex: "&[0-9 a-f]"
        permission: bungeefilter.colorbypass
        actions:
            remove: true
            message: '&cYou do not have permission for colors'

#Assumes all IP addresses are advertising and all URLs that do not include this server's name are advertising
    AntiAdvertising:
        regex: (^.*([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}).*$)|(^.*((mc|play)\.(.*)\.(com|net))|((.+)\.(.*(mine|craft).*)\.(com|net)).*$)
        ignores: (^.*minecraftserver.*$) |(^.*minecraftserver2.*$)
        actions:
            kick: '&cNo advertising other servers.'
            deny: true

#When a player uses the command /hub they will instead cast the command /server hub to the proxy. Denies the command so /hub isnt also sent
    CommandShortcut:
        regex: /hub
        actions:
            pcommand: /server hub
            deny: true

#When a player uses the command /shop they will instead send a command to the server they are on /warp shop.
    CommandShortcut2:
        regex: /shop
        actions:
            scommand: /warp shop
            deny: true

#Creates a proxywide broadcast command !bungeefilter.broadcast means the player needs the permission for the check to work
    CreateCommand:
        regex: /broadcast
        permission: "bungeefilter.broadcast"
        permissiontype: REQUIRED
        actions:
            alert: '&5[Broadcast]&a{player}&f:{arguments}'
            deny: true

#Shortcut for website URL can use either /url or /website
    CreateCommand2:
        regex: (/url)|(/website)
        actions:
            message: "&l&6[Check it out]&3 This server's website is &6www.minecraftserver.com"
            deny: true
```
