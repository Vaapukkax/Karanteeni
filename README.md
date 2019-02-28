#Matti Turpeinen

# Karanteeni
This README file is made to help understand how KaranteeniCore should be used

## Code examples of KaranteeniCore
### How to extend KaranteeniCore
```java
public class MyPlugin extends KaranteeniPlugin
{
  public MyPlugin()
  {
    //Does this plugin use the translator service of KaranteeniCore
    super(true);
  }
}
```

### Creating commands and using translation engine
MyCommand.java
```java
public class MyCommand extends AbstractCommand implements TranslationContainer
{
  public MyCommand()
  {
    super(MyPlugin.getPlugin(MyPlugin.class), "command", "usage", "description", Arrays.asList("possible","command","arguments"));
    registerTranslations();
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
  {
    //Returns a translation based on the users language if player
    sender.sendMessage(MyPlugin.getTranslator().getTranslation(MyPlugin.getPlugin(MyPlugin.class),
      sender, "sample-text"));
    
    if(args.length == 1)
      if(this.getRealParam(args[0]).equalsIgnoreCase("arguments"))
        sender.sendMessage(MyPlugin.getTranslator().getTranslation(MyPlugin.getPlugin(MyPlugin.class),
          sender, "cmd-args"));
  }
  
  @Override
  public void registerTranslations() 
  {
    MyPlugin.getTranslator().registerTranslation(
      MyPlugin.getPlugin(MyPlugin.class), "sample-text", "This is sample text");
    MyPlugin.getTranslator().registerTranslation(
      MyPlugin.getPlugin(MyPlugin.class), "cmd-args", "Translated command arguments!");
  }
}
```

### Getting default messages (strings)
```java
MyPlugin.getDefaultMsgs().noPermission(CommandSender);
MyPlugin.getDefaultMsgs().playerNotFound(CommandSender);
MyPlugin.getDefaultMsgs().defaultNotForConsole();
```

### Accessing database
```java
MyPlugin.getDatabaseConnector();
```

### Get nearby entities
```java
//Example location
Location loc = new Location(Bukkit.getWorld("world"), 100, 65, 635);
List<Entity> entities = MyPlugin.getEntityManager().getNearbyEntities(loc, 5);
List<Player> players = MyPlugin.getEntityManager().getNearbyPlayers(loc, 5);
Player nearestPlayer = MyPlugin.getEntityManager().getNearestPlayer(loc);
List<Animal> animals = MyPlugin.getEntityManager().getNearbyAnimals(loc, 5);
List<Monster> monsters = MyPlugin.getEntityManager().getNearbyMonsters(loc, 5);
// etc.
```

### Send animated bossbar to player
```java
@EventHandler
public void chat(AsyncPlayerChatEvent event)
{
  //Create any style bossbar
  BossBar bar = Bukkit.createBossBar(event.getMessage(), BarColor.YELLOW, BarStyle.SOLID);
  List<String> texts = new ArrayList<String>();
  
  for(int i = 0; i < event.getMessage().length; ++i)
  {
    char c = texts.get(i).charAt(0);
    String text = texts.get(i).substring(1);
    texts.add(text+c);
  }
  
  MyPlugin.getMessager().sendBossBar(event.getPlayer(), Sounds.NONE.get(), 5f, 3, true, bar, texts);
}
```

### Send title to player
```java
@EventHandler
public void chat(AsyncPlayerChatEvent event)
{
  MyPlugin.getMessager().sendTitle(0.1f, 0.1f, 1.2f, event.getPlayer(), event.getMessage(), "subtitle!", Sounds.NONE.get());
}
```

### Send message to player
```java
@EventHandler
public void chat(AsyncPlayerChatEvent event)
{
  MyPlugin.getMessager().sendMessage(players, Sounds.PLING_HIGH.get(), "This is a message with sound!");
  event.setCancelled(true);
}
```

### Create inventorymenu
```java
public class TestInv extends InventoryMenu
{
  public TestInv(Player player)
  {
    super(MyPlugin.getPlugin(MyPlugin.class), 
      player, //To who will this inventory be opened
      InventoryType.CHEST,
      18, //Size of inventory
      true, //Use the default empty items
      true, //Allow player to close the inventory
      "This is a title!");
  }
  
  @Override
  @EventHandler
  public void menuClick(InventoryClickEvent event)
  {
    //Remember to check that the used inventory item and player are valid!
    if(!isValid(event.getClickedInventory(), event.getCurrentItem(), (Player)event.getWhoClicked()) return;
    
    if(event.getItem().equals(getClose());
    {
      this.onClick(MenuClick.CLICK_SUCCESS);
      this.closeInventory();
    }
  }
  
  @Override
  protected void fillItems()
  {
    inventory.setItem(0, getClose());
  }
}

// TestInv inv = new TestInv(Bukkit.getOnlinePlayers().get(0));
// inv.openInventory(); //Open the inventory to a player in some class
```
