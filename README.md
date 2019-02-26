# CustomItemManager
A Minecraft Plugin that allows admins to save custom made items, including all MetaData such as coloured names and lore, 
and spawn them back in at will.
Perfect for RPG servers.

Download/Bug Reporting: https://www.spigotmc.org/resources/customitemmanager.65202/

## Commands

> /customitem OR /customitem help <commandname>

Displays the help text.

> /customitem item OR /customitem help item

Displays the help text for item management.

> /customitem item set <id>

Saves a custom item to the database.

> /customitem item spawn <id>

Spawns a custom item from the database.

> /customitem item delete <id>

Deletes a custom item from the database.

> /customitem item list

Lists all custom items in the database.

> /customitem save

Manually saves all items to the database. Automatically happens on server shutdown/restart.

## Alias: 
/ci >> /customitem (eg. /ci item set instead of /customitem item set)

## Permissions:

> customitemmanager.*
Permission for all commands.

> customitemmanager.help
Permission to use /customitem help.

> customitemmanager.item.set
Permission to use /customitem item set.

> customitemmanager.item.spawn
Permission to use/customitem item spawn.

> customitemmanager.item.delete
Permission to use /customitem item delete.

> customitemmanager.item.list
Permission to use /customitem item list.

> customitemmanager.save
Permission to use /customitem save.

## End of file.
Updated: 25/02/2019
